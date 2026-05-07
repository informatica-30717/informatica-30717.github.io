-- PDF-only cleanup for practice 1.
--
-- Root horizontal rules are Reveal slide separators in the source. They are
-- useful for slides but noisy in a handout, so PDF output drops them and starts
-- each main section on a new page. Quarto column layouts are comfortable in
-- slides, but cramped in print, so the filter also stacks those blocks in PDF.

local function remove_class(classes, name)
  local kept = pandoc.List()

  for _, class in ipairs(classes) do
    if class ~= name then
      kept:insert(class)
    end
  end

  return kept
end

function Div(div)
  if div.attributes.layout ~= nil then
    div.attributes.layout = nil
  end

  div.classes = remove_class(div.classes, "column")
  div.classes = remove_class(div.classes, "columns")

  return div
end

function Pandoc(doc)
  local blocks = pandoc.List()

  for _, block in ipairs(doc.blocks) do
    if block.t == "HorizontalRule" then
      -- Reveal slide separator: omit in PDF.
    else
      if block.t == "Header" and block.level == 2 then
        blocks:insert(pandoc.RawBlock("latex", "\\clearpage"))
      end

      blocks:insert(block)
    end
  end

  doc.blocks = blocks
  return doc
end
