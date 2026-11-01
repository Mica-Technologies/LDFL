# Regenerate LDFL's hero item art. Run from anywhere:
#   pwsh -File tools/art/textures.ps1            (writes the textures)
#   pwsh -File tools/art/textures.ps1 -Preview   (also writes an 8x preview sheet to tools/art/preview/)
# All art is original. See LdflArt.ps1 for the drawing primitives and the finishing pass.
param([switch]$Preview)

. "$PSScriptRoot\LdflArt.ps1"
$root = Resolve-Path "$PSScriptRoot\..\.."
$items = Join-Path $root 'src\main\resources\assets\ldfl\textures\items'
$written = @()

# --- Shared palette -------------------------------------------------------------------------
$steel = '#9AA4AF'; $steelDk = '#5A616B'; $gun = '#474D56'; $gunDk = '#2B2F35'
$brass = '#C8873A'; $brassLt = '#E8B04A'; $wood = '#8A6440'; $leather = '#5C3B26'
$cyan = '#5FE3F0'; $orange = '#E8892E'; $stone = '#7D8287'; $stoneDk = '#5E6266'; $glow = '#FFF1A8'

# --- Bolt Driver: a brass-bodied sidearm with a glowing coil, facing right ------------------
$b = New-Art
Add-Rect $b 3 4 8 4 $steel                 # top rail
Add-Rect $b 2 5 7 9 $brass                 # body
Add-Rect $b 8 5 13 6 $gun                  # barrel
Add-Rect $b 8 7 12 7 $gunDk                # barrel underside
Add-Rect $b 14 5 14 6 $steel               # muzzle
Add-Rect $b 3 7 5 8 $cyan                  # coil window
Add-Rect $b 2 10 4 13 $gunDk               # grip
Add-Rect $b 5 10 7 10 $gun                 # trigger guard
Set-Px $b 7 11 $gun
Complete-Art $b
Set-Px $b 4 7 '#C8FFFF'                    # coil glint (after bevel so it stays bright)
Save-Art $b "$items\bolt_driver.png"; $written += "$items\bolt_driver.png"

# --- Tinker's Wrench: brass grip, steel shaft, open jaw up-right ----------------------------
$b = New-Art
Add-Line $b 2 13 6 9 $brass 2              # grip
Add-Line $b 6 9 10 5 $steel 2              # shaft
Add-Rect $b 10 2 14 4 $steel               # jaw block
Set-Px $b 12 2 ''; Set-Px $b 12 3 ''       # open slot in the jaw
Complete-Art $b
Save-Art $b "$items\tinkers_wrench.png"; $written += "$items\tinkers_wrench.png"

# --- Cragfist: a stone gauntlet with orange fissures and an iron cuff -----------------------
$b = New-Art
Add-Grid $b @(
    '................',
    '.....NNNNNNNN...',
    '....SNSNSNSNSS..',
    '....SSSSSSSSSS..',
    '...SSSSSOSSSSS..',
    '...SSSSSOOSSSS..',
    '...SSSSSSSOSSS..',
    '...DSSSSSSSSSD..',
    '....DDDDDDDDDD..',
    '.....IIIIIIII...',
    '.....IXIIXIIX...',
    '.....IIIIIIII...',
    '......IIIIII....',
    '................') @{ N = '#A9AEB3'; S = $stone; D = $stoneDk; O = $orange; I = $steel; X = $steelDk } 0 1
Complete-Art $b
Save-Art $b "$items\cragfist.png"; $written += "$items\cragfist.png"

# --- Longshot Rifle: long diagonal rifle, wooden stock, scope with a cyan lens --------------
$b = New-Art
Add-Line $b 1 14 4 11 $wood 2              # stock
Add-Line $b 5 10 8 7 $gun 2                # receiver
Add-Line $b 9 6 14 1 $steelDk 1            # barrel
Add-Line $b 10 6 14 2 $gun 1               # barrel underside
Add-Line $b 6 7 9 4 $gunDk 1               # scope tube
Set-Px $b 10 3 $cyan                       # scope lens
Set-Px $b 6 11 $gunDk                      # trigger
Complete-Art $b
Save-Art $b "$items\longshot_rifle.png"; $written += "$items\longshot_rifle.png"

# --- Tuning Rod: brass rod ending in a tuning fork with glowing tips ------------------------
$b = New-Art
Add-Line $b 2 14 9 7 $brass 1
Add-Line $b 3 14 10 7 '#9C6428' 1
Add-Rect $b 9 6 10 7 $brassLt              # fork base
Add-Line $b 9 5 11 3 $brassLt 1            # tine 1
Add-Line $b 11 7 13 5 $brassLt 1           # tine 2
Complete-Art $b
Set-Px $b 12 2 $glow; Set-Px $b 14 4 $glow
Save-Art $b "$items\tuning_rod.png"; $written += "$items\tuning_rod.png"

# --- 3D model sheets -----------------------------------------------------------------------
# Bulwark Shield: 32x32. Front 12x22 at (0,0), back 12x22 at (12,0), edge strip 2x22 at (24,0),
# top/bottom strip 12x2 at (0,22), handle 4x6 at (26,0), visor 10x2 at (0,26).
$b = New-Art 32
Add-Rect $b 0 0 11 21 '#6C7887'            # front plate
Add-Rect $b 0 0 11 0 '#B9C3CE'; Add-Rect $b 0 0 0 21 '#B9C3CE'
Add-Rect $b 0 21 11 21 '#3E4652'; Add-Rect $b 11 0 11 21 '#3E4652'
Add-Rect $b 1 1 10 1 '#8995A3'             # inner bevel
Add-Rect $b 5 2 6 19 '#5F6A78'             # centre seam
Add-Rect $b 4 12 7 15 $brass; Add-Rect $b 5 13 6 14 $brassLt   # boss
foreach ($p in @(@(2, 2), @(9, 2), @(2, 19), @(9, 19))) { Set-Px $b $p[0] $p[1] '#D5DCE3' }
Add-Rect $b 12 0 23 21 '#3E4652'           # back plate
Add-Rect $b 12 6 23 7 $leather; Add-Rect $b 12 14 23 15 $leather   # straps
Add-Rect $b 24 0 25 21 '#525C69'           # edges
Add-Rect $b 0 22 11 23 '#525C69'
Add-Rect $b 26 0 29 5 $leather             # handle
Add-Rect $b 0 26 9 27 $cyan; Add-Rect $b 0 26 9 26 '#B8F8FF'   # visor band
Save-Art $b "$items\bulwark_shield_model.png"; $written += "$items\bulwark_shield_model.png"

# Bulwark Maul: 16x16. Head face (0,0)-(7,7), head side (8,0)-(15,7), haft (0,8)-(3,15),
# grip (4,8)-(7,15), pommel/cap metal (8,8)-(11,11), core glow (12,8)-(15,11).
$b = New-Art 16
Add-Rect $b 0 0 7 7 '#7D8794'; Add-Rect $b 0 0 7 0 '#A9B2BD'; Add-Rect $b 0 7 7 7 '#4C535C'
foreach ($p in @(@(1, 1), @(6, 1), @(1, 6), @(6, 6))) { Set-Px $b $p[0] $p[1] '#C9D3DE' }
Add-Rect $b 8 0 15 7 '#5F6873'; Add-Rect $b 8 3 15 4 '#4C535C'
Add-Rect $b 0 8 3 15 $wood; Add-Rect $b 1 8 1 15 '#A07A52'
Add-Rect $b 4 8 7 15 $leather; foreach ($y in 9, 11, 13, 15) { Add-Rect $b 4 $y 7 $y '#432A1A' }
Add-Rect $b 8 8 11 11 '#8D97A3'
Add-Rect $b 12 8 15 11 '#FF9A3C'; Add-Rect $b 13 9 14 10 '#FFD08A'
Save-Art $b "$items\bulwark_maul_model.png"; $written += "$items\bulwark_maul_model.png"

# Solace Staff: 16x16. Shaft (0,0)-(1,15), gold (2,0)-(5,3), gem (6,0)-(9,3), gem side (10,0)-(13,3).
$b = New-Art 16
Add-Rect $b 0 0 1 15 '#D9C7A0'; Add-Rect $b 1 0 1 15 '#B89F70'
Add-Rect $b 2 0 5 3 '#E8B04A'; Add-Rect $b 2 0 5 0 '#FFD27A'; Add-Rect $b 2 3 5 3 '#B8862A'
Add-Rect $b 6 0 9 3 '#5BE07A'; Set-Px $b 7 1 '#C8FFD0'; Set-Px $b 8 1 '#9CFFAE'
Add-Rect $b 10 0 13 3 '#3FB85C'
Save-Art $b "$items\solace_staff_model.png"; $written += "$items\solace_staff_model.png"

if ($Preview) {
    $prev = Join-Path $PSScriptRoot 'preview'
    New-Item -ItemType Directory -Force $prev | Out-Null
    Save-Preview $written (Join-Path $prev 'items.png') 8
    Write-Output "preview: $(Join-Path $prev 'items.png')"
}
Write-Output "wrote $($written.Count) textures"
