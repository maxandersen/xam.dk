---
title: 'Virtual Win32/Unix Printer Driver ?'
author: 'Max Rydahl Andersen'

tags: [ Java ]
orignallink: 'http://blog.xam.dk/?p=18'
---
<div><p>In the process of debugging some serious sun java problems I wanted to see what the heck the Java Print Services actually sent to my printer! <br><br>
In the hope someone else had thought of the same I Googled for "virtual printer driver", "gdi monitor" etc. etc. and came up with nothing :(<br><br>
I could find a bunch of virtual printer driver that could write the output to PDF, images, whatever - even dump the raw format produced by a printerdriver - but that is not what i want...<br><br>
I want to see the highlevel instructions that is send to the printerdriver - probably in form of GDI traces or something...does anyone know of such a beast ?  <br><br>
(I could also go in an modify the lower part of Java Print Services, but i would actually prefer to also be able to see what e..g Word sends to my printers - just for comparison sake.)</p></div>
