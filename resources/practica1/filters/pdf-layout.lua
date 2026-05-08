-- Practice 1 PDF-only layout cleanup.
--
-- Quarto column layouts are comfortable in slides, but cramped in print. Stack
-- them in the PDF while leaving the web/reveal source readable.

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

local function set_image_height(blocks, height)
  return blocks:walk({
    Image = function(image)
      image.attributes.height = height
      return image
    end,
  })
end

function Figure(figure)
  if figure.identifier == "fig-zoog-follow-mouse" then
    figure.content = set_image_height(figure.content, "50%")
  end

  return figure
end

function Image(image)
  if image.src:match("zoog_size%-static%.png$") then
    image.attributes.height = "50%"
  end

  return image
end
