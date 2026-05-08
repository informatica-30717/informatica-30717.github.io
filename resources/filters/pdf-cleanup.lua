-- Shared PDF-only cleanup for practice handouts.
--
-- Keep Reveal slide delimiters and web-friendly emoji in the QMD source, but
-- remove them from PDF output where they either add noise or miss font glyphs.

local icon_replacements = {
  "\240\159\167\160", -- brain
  "\240\159\146\161", -- light bulb
  "\226\154\160",     -- warning
  "\240\159\142\175", -- target
  "\240\159\147\140", -- pin
  "\240\159\145\137", -- pointing hand
  "\226\156\133",     -- check
  "\226\157\140",     -- cross
  "\226\150\182",     -- play
  "\240\159\148\180", -- red circle
  "\240\159\150\177", -- mouse
  "\226\164\147",     -- download arrow
  "\239\184\143",     -- emoji variation selector
}

local function sanitize_text(text)
  for _, icon in ipairs(icon_replacements) do
    text = text:gsub(icon, "")
  end

  return text
end

function Str(str)
  str.text = sanitize_text(str.text)

  if str.text == "" then
    return {}
  end

  return str
end

local function sanitize_inlines(inlines)
  local cleaned = pandoc.List()

  for _, inline in ipairs(inlines) do
    if inline.t == "Str" then
      local text = sanitize_text(inline.text)

      if text ~= "" then
        cleaned:insert(pandoc.Str(text))
      end
    else
      cleaned:insert(inline)
    end
  end

  while #cleaned > 0 and cleaned[1].t == "Space" do
    cleaned:remove(1)
  end

  return cleaned
end

local function inlines_to_text(inlines)
  local text = ""

  for _, inline in ipairs(inlines) do
    if inline.t == "Str" then
      text = text .. sanitize_text(inline.text)
    elseif inline.t == "Space" or inline.t == "SoftBreak" or inline.t == "LineBreak" then
      text = text .. " "
    end
  end

  text = text:gsub("%s+", " ")
  text = text:gsub("^%s+", "")
  text = text:gsub("%s+$", "")

  return text
end

function Para(para)
  local text = inlines_to_text(para.content)

  if text == "Solución" or text == "Solucion" or text == "Solución:" or text == "Solucion:" then
    return pandoc.Para({
      pandoc.RawInline("latex", "\\vspace{0.35em}\\noindent\\textbf{Solución:}"),
    })
  end

  return para
end

function Div(div)
  for key, value in pairs(div.attributes) do
    div.attributes[key] = sanitize_text(value)
  end

  return div
end

function Header(header)
  header.content = sanitize_inlines(header.content)

  if header.identifier ~= "" then
    local identifier = header.identifier
    header.identifier = ""

    return {
      header,
      pandoc.RawBlock("latex", "\\label{" .. identifier .. "}"),
    }
  end

  return header
end

function Pandoc(doc)
  local blocks = pandoc.List()
  local first_section_seen = false

  for _, block in ipairs(doc.blocks) do
    if block.t ~= "HorizontalRule" then
      if not first_section_seen and block.t == "Header" and block.level <= 2 then
        blocks:insert(pandoc.RawBlock("latex", "\\clearpage"))
        first_section_seen = true
      end

      blocks:insert(block)
    end
  end

  doc.blocks = blocks
  return doc
end
