#!/usr/bin/env bash
# Recompile SCSS -> public/assets/main.css after editing _sass/*.
# ponytail: compiled manually instead of via web-bundler to keep /assets/* URLs unchanged.
set -euo pipefail
cd "$(dirname "$0")"
node -e '
const sass=require("./node_modules/sass"),fs=require("fs");
const r=sass.compile("_sass/styles.scss",{style:"compressed",quietDeps:true,
  silenceDeprecations:["import","global-builtin","color-functions","mixed-decls","abs-percent","slash-div","legacy-js-api"]});
fs.writeFileSync("public/assets/main.css",r.css);
console.log("wrote public/assets/main.css",r.css.length,"bytes");
'
