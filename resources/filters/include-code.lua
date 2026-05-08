-- Replace code blocks with an include="path" attribute by the file contents.
--
-- This keeps snippets as normal source files while leaving the QMD compact:
--
-- ```{.java include="../resources/practicaX/snippets/example.java"}
-- ```

local function current_directory()
  if pandoc.system and pandoc.system.get_working_directory then
    return pandoc.system.get_working_directory()
  end

  return "."
end

local function file_exists(path)
  local file = io.open(path, "r")

  if file ~= nil then
    file:close()
    return true
  end

  return false
end

local function read_file(path)
  local file = assert(io.open(path, "r"), "Could not open code snippet: " .. path)
  local text = file:read("*a")
  file:close()

  return text
end

local function dirname(path)
  return pandoc.path.directory(path)
end

local function resolve_path(path)
  if pandoc.path.is_absolute(path) then
    return path
  end

  local candidates = pandoc.List({ path })
  local cwd = current_directory()

  if PANDOC_STATE and PANDOC_STATE.input_files and PANDOC_STATE.input_files[1] then
    candidates:insert(pandoc.path.join({ dirname(PANDOC_STATE.input_files[1]), path }))
  end

  candidates:insert(pandoc.path.join({ cwd, path }))

  if path:sub(1, 3) == "../" then
    candidates:insert(pandoc.path.join({ cwd, path:sub(4) }))
  end

  for _, candidate in ipairs(candidates) do
    if file_exists(candidate) then
      return candidate
    end
  end

  return path
end

function CodeBlock(block)
  local include = block.attributes.include

  if include == nil then
    return block
  end

  if FORMAT:match("latex") then
    return pandoc.RawBlock("latex", "\\PracticeCode{" .. include .. "}")
  end

  block.text = read_file(resolve_path(include)):gsub("%s+$", "")
  block.attributes.include = nil

  return block
end
