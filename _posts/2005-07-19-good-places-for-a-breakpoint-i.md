---
title: 'Good places for a breakpoint I'
author: 'Max Rydahl Andersen'

tags: [ Java ]
orignallink: 'http://blog.xam.dk/?p=49'
---
<div>
<br><br>
Used way to much time lately remembering some good places for a breakpoint while developing on  Eclipse - so to help my self and possibly others then without further comment:
<br><br>
To track invocation of any WorkBench action: 
<br><br>
org.eclipse.ui.internal.Workbench.runEventLoop()
<br><br>
To track invocation of any menu/keyboard action: 
<br><br>
ActionHandler.execute()
<br><br>
These two together with some good breakpoint conditions and you should be able to track down which plugin is actually responsible for a specific action in the UI - a great timesaver.
<br><br>
</div>
