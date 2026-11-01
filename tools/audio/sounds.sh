#!/usr/bin/env bash
# Synthesize every LDFL sound effect from maths, with ffmpeg's aevalsrc (sine tones, noise and
# envelopes). Nothing is sampled or recorded, so the audio is wholly original and MIT-clean.
#
#   bash tools/audio/sounds.sh        # needs ffmpeg with libvorbis on PATH
#
# Output: src/main/resources/assets/ldfl/sounds/<name>.ogg (mono, 44.1 kHz, Vorbis q4).
# sounds.json and LdflSounds.java list the same names; keep the three in step.
set -euo pipefail
cd "$(dirname "$0")/../.."
OUT=src/main/resources/assets/ldfl/sounds
mkdir -p "$OUT"

N='(random(0)*2-1)'   # white noise sample

# name  duration  expression  [extra ffmpeg audio filters]
gen() {
  local name="$1" dur="$2" expr="$3" extra="${4:-}"
  local fade
  fade=$(awk "BEGIN{print ($dur>0.1)?$dur-0.05:0}")
  local af="afade=t=out:st=${fade}:d=0.05"
  [ -n "$extra" ] && af="$extra,$af"
  local tmp
  tmp=$(mktemp --suffix=.wav)
  ffmpeg -hide_banner -loglevel error -y -f lavfi \
    -i "aevalsrc=exprs='$expr':s=44100:d=$dur" -af "$af" -ac 1 "$tmp"
  # Second pass: peak-normalise to -3 dBFS so every effect sits at a consistent level.
  local peak gain
  peak=$(ffmpeg -hide_banner -i "$tmp" -af volumedetect -f null - 2>&1 | grep -oE 'max_volume: [-0-9.]+' | grep -oE '[-0-9.]+$')
  gain=$(awk "BEGIN{print -3 - ($peak)}")
  ffmpeg -hide_banner -loglevel error -y -i "$tmp" -af "volume=${gain}dB" -c:a libvorbis -q:a 4 "$OUT/$name.ogg"
  rm -f "$tmp"
}

# --- Bulwark ---
gen barrier_raise 0.5 "sin(2*PI*(180+400*t)*t)*0.5*(1-exp(-20*t))*exp(-3*t)+sin(2*PI*1200*t)*0.08*exp(-6*t)"
gen barrier_hit   0.25 "sin(2*PI*900*t)*exp(-18*t)*0.5+sin(2*PI*1350*t)*exp(-22*t)*0.3+$N*exp(-40*t)*0.25"
gen barrier_break 0.8 "$N*exp(-5*t)*0.5+sin(2*PI*(600-500*t)*t)*exp(-4*t)*0.4" "highpass=f=300"
gen rush          0.5 "$N*sin(PI*t/0.5)*0.5" "lowpass=f=1200"
gen shockwave     0.6 "sin(2*PI*(120-80*t)*t)*exp(-5*t)*0.8+$N*exp(-10*t)*0.3" "lowpass=f=2000"
gen tectonic_slam 1.2 "sin(2*PI*(60-30*t)*t)*exp(-2.5*t)*0.9+$N*exp(-3*t)*0.5" "lowpass=f=800"

# --- Forgewright ---
gen sentry_deploy 0.6 "lt(t,0.04)*$N*0.6+gt(t,0.3)*lt(t,0.34)*$N*0.5+sin(2*PI*(300+600*t)*t)*0.2*exp(-3*t)"
gen sentry_fire   0.15 "sin(2*PI*(1800-6000*t)*t)*exp(-25*t)*0.5"
gen bolt_fire     0.18 "sin(2*PI*(1400-4000*t)*t)*exp(-20*t)*0.6+$N*exp(-50*t)*0.2"
gen slag_surge    1.0 "$N*0.45*(1-exp(-8*t))*exp(-1.5*t)+sin(2*PI*90*t)*0.3*exp(-2*t)" "lowpass=f=900"

# --- Solace ---
gen flask_burst   0.5 "sin(2*PI*1568*t)*exp(-8*t)*0.3+sin(2*PI*2093*t)*exp(-10*t)*0.2+$N*exp(-12*t)*0.2" "highpass=f=400"
gen sanctuary     1.6 "(sin(2*PI*523*t)+sin(2*PI*659*t)+sin(2*PI*784*t)+0.5*sin(2*PI*1046*t))*0.15*(1-exp(-6*t))*exp(-1.2*t)"
gen tailwind      0.7 "$N*sin(PI*t/0.7)*0.4+sin(2*PI*(300+700*t)*t)*0.12*sin(PI*t/0.7)" "lowpass=f=2500"

# --- Monolith ---
gen stone_skin    0.4 "$N*exp(-9*t)*0.5+sin(2*PI*110*t)*exp(-12*t)*0.4" "lowpass=f=1500"
gen upheaval      0.9 "$N*(1-exp(-10*t))*exp(-2.5*t)*0.7+sin(2*PI*55*t)*0.5*exp(-2*t)" "lowpass=f=400"
gen bedrock_anchor 1.0 "sin(2*PI*196*t)*exp(-3*t)*0.4+sin(2*PI*293*t)*exp(-4*t)*0.3+sin(2*PI*55*t)*exp(-2*t)*0.5"

# --- Kestrel ---
gen longshot      0.5 "$N*exp(-30*t)*0.8+sin(2*PI*(900-1200*t)*t)*exp(-6*t)*0.3"
gen marker_dart   0.2 "$N*exp(-30*t)*0.4+sin(2*PI*2400*t)*exp(-25*t)*0.2" "highpass=f=1500"
gen updraft       0.7 "$N*sin(PI*t/0.7)*0.4+sin(2*PI*(200+900*t)*t)*0.15*sin(PI*t/0.7)" "lowpass=f=2500"
gen long_vigil    1.2 "(sin(2*PI*440*t)*sin(2*PI*3*t)*0.2+sin(2*PI*660*t)*0.15)*(1-exp(-3*t))*exp(-1*t)"

# --- Chime ---
gen orb           0.3 "sin(2*PI*1318*t)*exp(-9*t)*0.35+sin(2*PI*2636*t)*exp(-14*t)*0.1"
gen resonance     0.8 "(sin(2*PI*784*t)*exp(-4*t)+gt(t,0.12)*sin(2*PI*988*t)*exp(-4*(t-0.12))+gt(t,0.24)*sin(2*PI*1175*t)*exp(-4*(t-0.24)))*0.25"
gen ward_bell     1.2 "sin(2*PI*660*t)*exp(-2.5*t)*0.3+sin(2*PI*1322*t)*exp(-3*t)*0.2+sin(2*PI*1782*t)*exp(-4*t)*0.15+sin(2*PI*2640*t)*exp(-6*t)*0.1"
gen crescendo     1.8 "(sin(2*PI*392*t)+sin(2*PI*494*t)+sin(2*PI*587*t)+sin(2*PI*784*t))*0.12*min(t/1.0,1)*exp(-2*max(t-1.0,0))"

# --- Shared ---
gen ult_ready     0.6 "lt(t,0.3)*sin(2*PI*880*t)*exp(-6*t)*0.3+gt(t,0.15)*sin(2*PI*1318*t)*exp(-6*(t-0.15))*0.3"
gen mending       0.6 "sin(2*PI*1046*t)*exp(-5*t)*0.25+gt(t,0.1)*sin(2*PI*1568*t)*exp(-6*(t-0.1))*0.2"

ls "$OUT" | wc -l
