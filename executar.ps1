$ErrorActionPreference = 'Stop'
$diretorioProjeto = Split-Path -Parent $MyInvocation.MyCommand.Path
& (Join-Path $diretorioProjeto 'compilar.ps1')
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$repositorioJavaFx = Join-Path $env:USERPROFILE '.m2\repository\org\openjfx'
$versao = Get-ChildItem -LiteralPath (Join-Path $repositorioJavaFx 'javafx-controls') -Directory |
  Sort-Object { [version]$_.Name } -Descending |
  Select-Object -First 1 -ExpandProperty Name
$modulos = @('javafx-base', 'javafx-graphics', 'javafx-controls', 'javafx-fxml')
$caminhoModulos = ($modulos | ForEach-Object {
  Join-Path $repositorioJavaFx "$_\$versao\$_-$versao-win.jar"
}) -join ';'

& java --enable-native-access=javafx.graphics --module-path $caminhoModulos `
  --add-modules javafx.controls,javafx.fxml `
  -cp (Join-Path $diretorioProjeto 'out') Principal
