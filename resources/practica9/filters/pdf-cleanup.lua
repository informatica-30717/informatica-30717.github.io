-- PDF-only cleanup for practice 9.
--
-- Keep the source readable for web/reveal authors:
-- - root-level horizontal rules are Reveal slide separators, so remove them
--   from the PDF instead of wrapping every "---" in format conditionals;
-- - root-level level-2 headings are the main sections, so start each one on a
--   new PDF page. With a table of contents, this also keeps the first section
--   from crowding the index page.

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
