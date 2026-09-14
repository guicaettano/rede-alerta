$ErrorActionPreference = 'Stop'
& (Join-Path (Split-Path -Parent $MyInvocation.MyCommand.Path) 'compilar.ps1') -Executar
