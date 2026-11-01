# LDFL pixel-art toolkit. Dot-source it: . "$PSScriptRoot\LdflArt.ps1"
#
# Every LDFL texture is original and drawn in code, so the art stays MIT-clean and reproducible
# (decision D15 in the plan). Draw flat silhouettes with the primitives, then call Complete-Art:
# it adds a one-pixel dark outline and bevels each shape (lighter top-left, darker bottom-right),
# which gives every icon the same crisp, consistent finish.

Add-Type -AssemblyName System.Drawing

function ConvertTo-Color([string]$hex) {
    if (-not $hex) { return [System.Drawing.Color]::Transparent }
    return [System.Drawing.ColorTranslator]::FromHtml($hex)
}

function New-Art([int]$size = 16) {
    $b = New-Object System.Drawing.Bitmap $size, $size, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    for ($x = 0; $x -lt $size; $x++) { for ($y = 0; $y -lt $size; $y++) { $b.SetPixel($x, $y, [System.Drawing.Color]::Transparent) } }
    return $b
}

function Set-Px($b, [int]$x, [int]$y, [string]$hex) {
    if ($x -ge 0 -and $y -ge 0 -and $x -lt $b.Width -and $y -lt $b.Height) { $b.SetPixel($x, $y, (ConvertTo-Color $hex)) }
}

function Add-Rect($b, [int]$x0, [int]$y0, [int]$x1, [int]$y1, [string]$hex) {
    for ($x = $x0; $x -le $x1; $x++) { for ($y = $y0; $y -le $y1; $y++) { Set-Px $b $x $y $hex } }
}

# A line of the given thickness, using integer DDA.
function Add-Line($b, [int]$x0, [int]$y0, [int]$x1, [int]$y1, [string]$hex, [int]$thick = 1) {
    $n = [Math]::Max([Math]::Abs($x1 - $x0), [Math]::Abs($y1 - $y0))
    for ($i = 0; $i -le $n; $i++) {
        $t = if ($n -eq 0) { 0 } else { $i / $n }
        $x = [int][Math]::Round($x0 + ($x1 - $x0) * $t)
        $y = [int][Math]::Round($y0 + ($y1 - $y0) * $t)
        for ($dx = 0; $dx -lt $thick; $dx++) { for ($dy = 0; $dy -lt $thick; $dy++) { Set-Px $b ($x + $dx) ($y + $dy) $hex } }
    }
}

function Add-Disc($b, [double]$cx, [double]$cy, [double]$r, [string]$hex) {
    for ($x = 0; $x -lt $b.Width; $x++) { for ($y = 0; $y -lt $b.Height; $y++) {
        if ((($x - $cx) * ($x - $cx) + ($y - $cy) * ($y - $cy)) -le ($r * $r)) { Set-Px $b $x $y $hex }
    } }
}

# Draw a grid of characters with a palette hashtable; '.' is left untouched.
function Add-Grid($b, [string[]]$rows, [hashtable]$palette, [int]$ox = 0, [int]$oy = 0) {
    for ($y = 0; $y -lt $rows.Count; $y++) {
        for ($x = 0; $x -lt $rows[$y].Length; $x++) {
            $ch = [string]$rows[$y][$x]
            if ($ch -ne '.' -and $palette.ContainsKey($ch)) { Set-Px $b ($ox + $x) ($oy + $y) $palette[$ch] }
        }
    }
}

function Get-Shade([System.Drawing.Color]$c, [double]$f) {
    $clamp = { param($v) [int][Math]::Max(0, [Math]::Min(255, $v)) }
    if ($f -ge 1) {
        return [System.Drawing.Color]::FromArgb($c.A, (& $clamp ($c.R + (255 - $c.R) * ($f - 1))),
            (& $clamp ($c.G + (255 - $c.G) * ($f - 1))), (& $clamp ($c.B + (255 - $c.B) * ($f - 1))))
    }
    return [System.Drawing.Color]::FromArgb($c.A, (& $clamp ($c.R * $f)), (& $clamp ($c.G * $f)), (& $clamp ($c.B * $f)))
}

# Outline + bevel. A pixel is "edge" where a 4-neighbour differs in colour or is transparent.
function Complete-Art($b, [string]$outline = '#14171B', [switch]$NoOutline) {
    $w = $b.Width; $h = $b.Height
    $src = New-Object 'System.Drawing.Color[,]' $w, $h
    for ($x = 0; $x -lt $w; $x++) { for ($y = 0; $y -lt $h; $y++) { $src[$x, $y] = $b.GetPixel($x, $y) } }
    $solid = { param($x, $y) ($x -ge 0 -and $y -ge 0 -and $x -lt $w -and $y -lt $h -and $src[$x, $y].A -gt 0) }
    for ($x = 0; $x -lt $w; $x++) { for ($y = 0; $y -lt $h; $y++) {
        $c = $src[$x, $y]
        if ($c.A -eq 0) { continue }
        $same = { param($nx, $ny) (& $solid $nx $ny) -and ($src[$nx, $ny].ToArgb() -eq $c.ToArgb()) }
        if (-not (& $same ($x - 1) $y) -or -not (& $same $x ($y - 1))) { $b.SetPixel($x, $y, (Get-Shade $c 1.25)) }
        elseif (-not (& $same ($x + 1) $y) -or -not (& $same $x ($y + 1))) { $b.SetPixel($x, $y, (Get-Shade $c 0.72)) }
    } }
    if ($NoOutline) { return }
    $oc = ConvertTo-Color $outline
    for ($x = 0; $x -lt $w; $x++) { for ($y = 0; $y -lt $h; $y++) {
        if ($src[$x, $y].A -gt 0) { continue }
        if ((& $solid ($x - 1) $y) -or (& $solid ($x + 1) $y) -or (& $solid $x ($y - 1)) -or (& $solid $x ($y + 1))) {
            $b.SetPixel($x, $y, $oc)
        }
    } }
}

function Save-Art($b, [string]$path) {
    $dir = Split-Path $path
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Force $dir | Out-Null }
    $b.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $b.Dispose()
}

# Nearest-neighbour contact sheet of PNGs for review, $scale x enlarged.
function Save-Preview([string[]]$paths, [string]$out, [int]$scale = 8) {
    $tiles = $paths | ForEach-Object { [System.Drawing.Image]::FromFile($_) }
    $cell = [int](($tiles | ForEach-Object { [Math]::Max($_.Width, $_.Height) } | Measure-Object -Maximum).Maximum * $scale + 8)
    $cols = [Math]::Min(6, $tiles.Count); $rows = [Math]::Ceiling($tiles.Count / $cols)
    $sheet = [System.Drawing.Bitmap]::new([int]($cols * $cell), [int]($rows * $cell))
    $g = [System.Drawing.Graphics]::FromImage($sheet)
    $g.Clear([System.Drawing.Color]::FromArgb(255, 120, 132, 120))
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
    for ($i = 0; $i -lt $tiles.Count; $i++) {
        $t = $tiles[$i]
        $g.DrawImage($t, ($i % $cols) * $cell + 4, [Math]::Floor($i / $cols) * $cell + 4, $t.Width * $scale, $t.Height * $scale)
        $t.Dispose()
    }
    $g.Dispose(); $sheet.Save($out, [System.Drawing.Imaging.ImageFormat]::Png); $sheet.Dispose()
}
