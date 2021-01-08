#!/bin/bash
set -euo pipefail

ts-node tools/gengraph.ts | dot -Tpdf > tools/graph.pdf
