param([switch]$Executar)
$ErrorActionPreference = 'Stop'
$diretorioProjeto = Split-Path -Parent $MyInvocation.MyCommand.Path
$versaoCompilador = (& javac -version 2>&1 | Out-String)
$versaoRuntime = (& java -version 2>&1 | Out-String)
if ($versaoCompilador -notmatch '(?:javac\s+)(?:1\.)?(\d+)') { throw 'JDK nao encontrado.' }
$majorJava = [int]$Matches[1]
if ($versaoRuntime -notmatch 'version\s+"(?:1\.)?(\d+)' -or [int]$Matches[1] -ne $majorJava) {
  throw 'java e javac devem usar a mesma versao principal.'
}
$opcoesJava = @()
$repositorioJavaFx = Join-Path $env:USERPROFILE '.m2\repository\org\openjfx'
$diretorioControles = Join-Path $repositorioJavaFx 'javafx-controls'

if ($majorJava -gt 8) {
if (-not (Test-Path -LiteralPath $diretorioControles)) {
  throw 'JavaFX nao encontrado em ~/.m2/repository/org/openjfx.'
}

$versao = Get-ChildItem -LiteralPath $diretorioControles -Directory |
  Where-Object { $_.Name -match '^\d+(\.\d+)*$' -and ([version]$_.Name).Major -le $majorJava } |
  Sort-Object { [version]$_.Name } -Descending |
  Select-Object -First 1 -ExpandProperty Name
if (-not $versao) { throw 'Nao ha JavaFX compativel no repositorio local.' }

$modulos = @('javafx-base', 'javafx-graphics', 'javafx-controls', 'javafx-fxml')
$jars = foreach ($modulo in $modulos) {
  $jar = Join-Path $repositorioJavaFx "$modulo\$versao\$modulo-$versao-win.jar"
  if (-not (Test-Path -LiteralPath $jar)) {
    throw "Modulo ausente: $jar"
  }
  $jar
}
$caminhoModulos = $jars -join ';'
$opcoesJava = @('--module-path', $caminhoModulos, '--add-modules', 'javafx.controls,javafx.fxml')
} else {
  $versao = '8 (deve estar incluido no JDK)'
}

$diretorioSaida = Join-Path $diretorioProjeto 'out'
if (Test-Path -LiteralPath $diretorioSaida) {
  if ((Get-Item -LiteralPath $diretorioSaida).Attributes -band [IO.FileAttributes]::ReparsePoint) { throw 'out nao pode ser um link.' }
  if ((Split-Path -Parent ([IO.Path]::GetFullPath($diretorioSaida))) -ne [IO.Path]::GetFullPath($diretorioProjeto)) { throw 'Saida fora do projeto.' }
  Remove-Item -LiteralPath $diretorioSaida -Recurse -Force
}
New-Item -ItemType Directory -Path $diretorioSaida | Out-Null

$fontes = @(
  (Join-Path $diretorioProjeto 'Principal.java')
) + @(
  Get-ChildItem -LiteralPath (Join-Path $diretorioProjeto 'model') -Filter '*.java' -File |
    Select-Object -ExpandProperty FullName
) + @(
  Get-ChildItem -LiteralPath (Join-Path $diretorioProjeto 'controller') -Filter '*.java' -File |
    Select-Object -ExpandProperty FullName
) + @(
  Get-ChildItem -LiteralPath (Join-Path $diretorioProjeto 'util') -Filter '*.java' -File |
    Select-Object -ExpandProperty FullName
)

& javac -encoding US-ASCII @opcoesJava -d $diretorioSaida $fontes
if ($LASTEXITCODE -ne 0) { throw 'Falha na compilacao.' }

Copy-Item -LiteralPath (Join-Path $diretorioProjeto 'view') `
  -Destination (Join-Path $diretorioSaida 'view') -Recurse
Copy-Item -LiteralPath (Join-Path $diretorioProjeto 'img') `
  -Destination (Join-Path $diretorioSaida 'img') -Recurse

Write-Host "Compilacao concluida com JavaFX $versao."
if ($Executar) {
  if ($majorJava -ge 24) { $opcoesJava += '--enable-native-access=javafx.graphics' }
  & java @opcoesJava -cp $diretorioSaida Principal
  if ($LASTEXITCODE -ne 0) { throw 'Falha na execucao.' }
}
