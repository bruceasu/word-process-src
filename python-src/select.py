#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys

yj = ['aa', 'ah', 'ai', 'an', 'ao',
      'ba', 'bb', 'bc', 'bd', 'bf', 'bg', 'bh', 'bi', 'bj', 'bk', 'bm', 'bn', 'bo', 'bp', 'bu', 'bw',
      'ca', 'cc', 'cd', 'ce', 'cf', 'cg', 'ch', 'ci', 'cj', 'co', 'cr', 'cs', 'cu', 'cv', 'cy', 'cz',
      'da', 'dc', 'dd', 'de', 'df', 'dg', 'dh', 'di', 'dj', 'dk', 'dm', 'dn', 'do', 'dp', 'dq', 'dr', 'ds', 'du', 'dv', 'dw', 'dx', 'dy', 'dz',
      'ee', 'ei', 'en', 'er',
      'fa', 'ff', 'fg', 'fh', 'fj', 'fo', 'fu', 'fw', 'fz',
      'ga', 'gc', 'gd', 'ge', 'gf', 'gg', 'gh', 'gj', 'gk', 'gl', 'go', 'gr', 'gs', 'gu', 'gv', 'gw', 'gx', 'gy', 'gz',
      'ha', 'hc', 'hd', 'he', 'hf', 'hg', 'hh', 'hj', 'hk', 'hl', 'ho', 'hr', 'hs', 'hu', 'hv', 'hw', 'hx', 'hy', 'hz',
      'ia', 'ic', 'id', 'ie', 'if', 'ig', 'ih', 'ii', 'ij', 'ik', 'il', 'io', 'ir', 'is', 'iu', 'iv', 'ixiy', 'iz',
      'jb', 'ji', 'jk', 'jl', 'jm', 'jn', 'jp', 'jq', 'jr', 'js', 'jt', 'ju', 'jx', 'jy',
      'ka', 'kc', 'kd', 'ke', 'kf', 'kg', 'kh', 'kj', 'kk', 'kl', 'ko', 'kr', 'ks', 'ku', 'kv', 'kw', 'kx', 'ky', 'kz',
      'la', 'lb', 'lc', 'ld', 'le', 'lf', 'lg', 'lh', 'li', 'lj', 'lk', 'll', 'lm', 'ln', 'lo', 'lp', 'lq', 'lr', 'ls', 'lt', 'lu', 'lv', 'lw', 'lx', 'ly', 'lz',
      'ma', 'mb', 'mc', 'md', 'me', 'mf', 'mg', 'mh', 'mi', 'mj', 'mk', 'mm', 'mn', 'mo', 'mp', 'mq', 'mu', 'mw', 'mz',
      'na', 'nb', 'nc', 'nd', 'ne', 'nf', 'ng', 'nh', 'ni', 'nj', 'nk', 'nl', 'nm', 'nn', 'no', 'np', 'nq', 'nr', 'ns', 'nt', 'nu', 'nv', 'nw', 'nz',
      'og', 'om', 'on', 'oo', 'ou',
      'pa', 'pb', 'pc', 'pd', 'pf', 'pg', 'ph', 'pi', 'pj', 'pk', 'pm', 'pn', 'po', 'pp', 'pu', 'pw', 'pz', 'qb',
      'qi', 'qk', 'ql', 'qm', 'qn', 'qp', 'qq', 'qr', 'qs', 'qt', 'qu', 'qx', 'qy',
      'rc', 're', 'rf', 'rg', 'rh', 'ri', 'rj', 'ro', 'rr', 'rs', 'ru', 'rv', 'ry', 'rz',
      'sa', 'sc', 'sd', 'se', 'sf', 'sg', 'sh', 'si', 'sj', 'so', 'sr', 'ss', 'su', 'sv', 'sy', 'sz',
      'ta', 'tc', 'td', 'te', 'tf', 'tg', 'th', 'ti', 'tj', 'tk', 'tm', 'tn', 'to', 'tp', 'tr', 'ts', 'tu', 'tv', 'ty', 'tz',
      'ua', 'uc', 'ud', 'ue', 'uf', 'ug', 'uh', 'ui', 'uj', 'uk', 'ul', 'uo', 'ur', 'uu', 'uv', 'uw', 'ux', 'uy', 'uz',
      'va', 'vc', 'vd', 've', 'vf', 'vg', 'vh', 'vi', 'vj', 'vk', 'vl', 'vo', 'vr', 'vs', 'vu', 'vv', 'vx', 'vy', 'vz',
      'wa', 'wd', 'wf', 'wg', 'wh', 'wj', 'wo', 'wu', 'ww',
      'xb', 'xi', 'xk', 'xl', 'xm', 'xn', 'xp', 'xq', 'xr', 'xs', 'xt', 'xu', 'xx', 'xy',
      'ya', 'yb', 'yc', 'ye', 'yh', 'yi', 'yj', 'yk', 'yo', 'yr', 'ys', 'yt', 'yu', 'yy', 'yz',
      'za', 'zc', 'zd', 'ze', 'zf', 'zg', 'zh', 'zi', 'zj', 'zo', 'zr', 'zs', 'zu', 'zv', 'zw', 'zy', 'zz']

all = []
for i in "abcdefghijklmnopqrstuvwxyz":
    for j in "abcdefghijklmnopqrstuvwxyz":
         k = i + j
         if k not in yj:
               print k
print 'done!'

