---
title: 'EventHandler.java is Good but not perfect'
author: 'Max Rydahl Andersen'

tags: [ Java ]
orignallink: 'http://blog.xam.dk/?p=22'
---
<div><p>Just "discovered" the newly (in JDK 1.4) added java.beans.EventHandler which allows you to do stuff like:<br><br><code><br>
but.addActionListener(<br>
&#160;&#160;&#160;(ActionListener)EventHandler.create(ActionListener.class, target, "myButtPressed", ""))<br></code><br><br>
which when <code>but</code> is pressed the method called <code>setMyButtPressed(ActionPerformed event)</code> will be called.<br><br>
This stuff actually safes alot of typing AND memory usage + even gain some performance if the HotSpot is nice to you ;)<br><br>
That was the good thing - the BAD thing is that EventHandler.java only works for public properties! And that is bad since it would break good old encapsulation....<br><br>
So, does anyone have a EventHandler replacement class somewhere ? One that allows any method to be called when a certain event occur (no matter it's private, protected, default or public) ?</p></div>
