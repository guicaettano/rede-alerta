$ErrorActionPreference = 'Stop'
$diretorioProjeto = Split-Path -Parent $MyInvocation.MyCommand.Path
$repositorioJavaFx = Join-Path $env:USERPROFILE '.m2\repository\org\openjfx'
$diretorioControles = Join-Path $repositorioJavaFx 'javafx-controls'

if (-not (Test-Path -LiteralPath $diretorioControles)) {
  throw 'JavaFX não encontrado em ~/.m2/repository/org/openjfx.'
}

$versao = Get-ChildItem -LiteralPath $diretorioControles -Directory |
  Sort-Object { [version]$_.Name } -Descending |
  Select-Object -First 1 -ExpandProperty Name

$modulos = @('javafx-base', 'javafx-graphics', 'javafx-controls', 'javafx-fxml')
$jars = foreach ($modulo in $modulos) {
  $jar = Join-Path $repositorioJavaFx "$modulo\$versao\$modulo-$versao-win.jar"
  if (-not (Test-Path -LiteralPath $jar)) {
    throw "Módulo ausente: $jar"
  }
  $jar
}
$caminhoModulos = $jars -join ';'

$diretorioSaida = Join-Path $diretorioProjeto 'out'
if (Test-Path -LiteralPath $diretorioSaida) {
  Remove-Item -LiteralPath $diretorioSaida -Recurse -Force
}
New-Item -ItemType Directory -Path $diretorioSaida | Out-Null

$fontes = @(
  (Join-Path $diretorioProjeto 'Principal.java')
) + @(
  Get-ChildItem -LiteralPath (Join-Path $diretorioProjeto 'model') -Filter '*.java' -File |
    Select-Object -ExpandProperty FullName
) + @(
  Get-ChildItem -LiteralPath (Join-Path $diretorioProjeto 'control') -Filter '*.java' -File |
    Select-Object -ExpandProperty FullName
)

& javac -encoding UTF-8 --module-path $caminhoModulos `
  --add-modules javafx.controls,javafx.fxml -d $diretorioSaida $fontes
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Copy-Item -LiteralPath (Join-Path $diretorioProjeto 'view') `
  -Destination (Join-Path $diretorioSaida 'view') -Recurse
Copy-Item -LiteralPath (Join-Path $diretorioProjeto 'img') `
  -Destination (Join-Path $diretorioSaida 'img') -Recurse

Write-Host "Compilação concluída com JavaFX $versao."
