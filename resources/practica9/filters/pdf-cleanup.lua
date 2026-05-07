-- PDF-only cleanup for practice 9.
--
-- Keep the source readable for web/reveal authors:
-- - root-level horizontal rules are Reveal slide separators, so remove them
--   from the PDF instead of wrapping every "---" in format conditionals;
-- - root-level level-2 headings are the main sections, so start each one
--   after the first on a new PDF page.

function Pandoc(doc)
  local blocks = pandoc.List()
  local first_section = true

  for _, block in ipairs(doc.blocks) do
    if block.t == "HorizontalRule" then
      -- Reveal slide separator: omit in PDF.
    else
      if block.t == "Header" and block.level == 2 then
        if first_section then
          first_section = false
        else
          blocks:insert(pandoc.RawBlock("latex", "\\clearpage"))
        end
      end

      blocks:insert(block)
    end
  end

  doc.blocks = blocks
  return doc
end
