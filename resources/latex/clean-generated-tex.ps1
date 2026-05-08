param(
  [Parameter(ValueFromRemainingArguments = $true)]
  [string[]] $Path
)

$utf8NoBom = [System.Text.UTF8Encoding]::new($false)

if (-not $Path -or $Path.Count -eq 0) {
  $repoRoot = Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..\..')
  $Path = @(
    Get-ChildItem -Path (Join-Path $repoRoot 'practicas') -Filter 'practica*.tex' |
      ForEach-Object { $_.FullName }
  )
}

foreach ($item in $Path) {
  $resolved = Resolve-Path -LiteralPath $item
  $text = [System.IO.File]::ReadAllText($resolved, [System.Text.Encoding]::UTF8)
  $text = $text -replace "`r`n", "`n"

  $headingPattern = '(?m)^(\\(?:section|subsection|subsubsection|paragraph|subparagraph)\*?(?:\[[^\n]*\])?\{[^\n]*\})\n\s*\n(\\label\{[^\n]+\})'
  $text = [regex]::Replace($text, $headingPattern, '$1' + "`n" + '$2')

  $text = [regex]::Replace(
    $text,
    '(?ms)\\begin\{figure\}.*?\\end\{figure\}%?',
    {
      param($match)

      $lines = $match.Value -split "`n"
      $kept = foreach ($line in $lines) {
        $trimmedRight = $line.TrimEnd()

        if ($trimmedRight.Trim().Length -gt 0) {
          $trimmedRight
        }
      }

      $kept -join "`n"
    }
  )

  $text = [regex]::Replace(
    $text,
    '(?m)^Soluci(?:ó|o)n\s*$',
    '\vspace{0.35em}\noindent\textbf{Solución:}'
  )

  [System.IO.File]::WriteAllText($resolved, $text, $utf8NoBom)
}
