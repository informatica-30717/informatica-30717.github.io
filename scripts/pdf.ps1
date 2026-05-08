param(
  [Parameter(Position = 0, ValueFromRemainingArguments = $true)]
  [string[]]$Arguments = @()
)

$ErrorActionPreference = "Continue"

$repoRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot "..") -ErrorAction Stop).Path
$pythonScript = Join-Path $repoRoot "scripts\build-practice-tex.py"
$Targets = @()
$Force = $false
$Clean = $false
$All = $false

foreach ($argument in $Arguments) {
  switch -Regex ($argument.ToLowerInvariant()) {
    "^(-|--|/)force$" {
      $Force = $true
      continue
    }
    "^(-|--|/)clean$" {
      $Clean = $true
      continue
    }
    "^(-|--|/)all$" {
      $All = $true
      continue
    }
    default {
      $Targets += $argument
    }
  }
}

function Resolve-PracticeTarget {
  param([string]$Target)

  $normalized = $Target.Trim()
  $lower = $normalized.ToLowerInvariant()

  if ($lower -in @("all", "*")) {
    return "all"
  }

  if ($lower -match "^p?(\d+)$") {
    return "practicas/practica$($Matches[1]).tex"
  }

  if ($lower -match "^practica(\d+)(\.(tex|qmd))?$") {
    return "practicas/practica$($Matches[1]).tex"
  }

  return $normalized
}

function Quote-ProcessArgument {
  param([string]$Value)

  if ($Value -match "[\s`"]") {
    return '"' + ($Value -replace '"', '\"') + '"'
  }

  return $Value
}

function Invoke-PdfBuild {
  param([string[]]$Arguments)

  $quotedArguments = @($pythonScript) + $Arguments | ForEach-Object {
    Quote-ProcessArgument $_
  }

  $stdout = New-TemporaryFile
  $stderr = New-TemporaryFile

  try {
    $process = Start-Process `
      -FilePath "python" `
      -ArgumentList $quotedArguments `
      -WorkingDirectory $repoRoot `
      -RedirectStandardOutput $stdout.FullName `
      -RedirectStandardError $stderr.FullName `
      -WindowStyle Hidden `
      -Wait `
      -PassThru

    Get-Content -LiteralPath $stdout.FullName | Write-Output
    Get-Content -LiteralPath $stderr.FullName | Write-Output

    $script:PdfBuildExitCode = $process.ExitCode
  } finally {
    Remove-Item -LiteralPath $stdout.FullName, $stderr.FullName -Force -ErrorAction SilentlyContinue
  }
}

$oldBuild = $env:QUARTO_BUILD_TEX
$oldForce = $env:QUARTO_TEX_FORCE
$oldClean = $env:QUARTO_TEX_CLEAN
$oldTargets = $env:QUARTO_TEX_TARGETS
$script:PdfBuildExitCode = 1
$hadNativePreference = Test-Path Variable:\PSNativeCommandUseErrorActionPreference
if ($hadNativePreference) {
  $oldNativePreference = $PSNativeCommandUseErrorActionPreference
} else {
  $oldNativePreference = $null
}

try {
  $PSNativeCommandUseErrorActionPreference = $false
  $env:QUARTO_BUILD_TEX = "1"

  if ($Force) {
    $env:QUARTO_TEX_FORCE = "1"
  } else {
    Remove-Item Env:\QUARTO_TEX_FORCE -ErrorAction SilentlyContinue
  }

  if ($Clean) {
    $env:QUARTO_TEX_CLEAN = "1"
  } else {
    Remove-Item Env:\QUARTO_TEX_CLEAN -ErrorAction SilentlyContinue
  }

  $resolvedTargets = @()

  if ($All) {
    $resolvedTargets = @("all")
  } elseif ($Targets.Count -gt 0) {
    $resolvedTargets = @($Targets | ForEach-Object { Resolve-PracticeTarget $_ })
  }

  if ($resolvedTargets -contains "all") {
    $env:QUARTO_TEX_TARGETS = "all"
    Invoke-PdfBuild @()
  } elseif ($resolvedTargets.Count -gt 0) {
    Remove-Item Env:\QUARTO_TEX_TARGETS -ErrorAction SilentlyContinue
    Invoke-PdfBuild $resolvedTargets
  } else {
    Remove-Item Env:\QUARTO_TEX_TARGETS -ErrorAction SilentlyContinue
    Invoke-PdfBuild @()
  }

  $exitCode = $script:PdfBuildExitCode
  $global:LASTEXITCODE = $exitCode

  if ($exitCode -ne 0) {
    throw "PDF build failed with exit code $exitCode."
  }
} finally {
  if ($hadNativePreference) {
    $PSNativeCommandUseErrorActionPreference = $oldNativePreference
  } else {
    Remove-Item Variable:\PSNativeCommandUseErrorActionPreference -ErrorAction SilentlyContinue
  }

  if ($null -eq $oldBuild) {
    Remove-Item Env:\QUARTO_BUILD_TEX -ErrorAction SilentlyContinue
  } else {
    $env:QUARTO_BUILD_TEX = $oldBuild
  }

  if ($null -eq $oldForce) {
    Remove-Item Env:\QUARTO_TEX_FORCE -ErrorAction SilentlyContinue
  } else {
    $env:QUARTO_TEX_FORCE = $oldForce
  }

  if ($null -eq $oldClean) {
    Remove-Item Env:\QUARTO_TEX_CLEAN -ErrorAction SilentlyContinue
  } else {
    $env:QUARTO_TEX_CLEAN = $oldClean
  }

  if ($null -eq $oldTargets) {
    Remove-Item Env:\QUARTO_TEX_TARGETS -ErrorAction SilentlyContinue
  } else {
    $env:QUARTO_TEX_TARGETS = $oldTargets
  }
}
