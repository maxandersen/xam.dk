---
title: 'Swing requestFocus blues'
author: 'Max Rydahl Andersen'

tags: [ Java ]
orignallink: 'http://blog.xam.dk/?p=9'
---
<div><p>hmm...was doing some more Swing coding on Hibern8IDE and came across a never ending story - How do you ensure that some component get the first focus when you open up a jdialog, jframe or unhide panel ?<br><br>
The Component.requestFocus() works great if the compoent is already visible, but often the component is not yet visible and when you make it visible (e.g. someframe.setVisible()) you can't guarantee that it will happen.<br><br>
The best solution I have found yet is to make an invokeLater() call that makes the correct requestFocus call....but there gotta be another way! <br><br>
The docs for the new focus manager in JDK 1.4 all talk about the "great new focus manager" but the only problem I got with the old one seem still to be present in the new one....<br><br>
So, how would you ensure that your a certain component in a JPanel gets the focus when the parent panel gets the focus ? Is there other more declaritive ways than calling table.requestFocus(); just after calling setVisible(true) or in an invokeLater ?</p></div>
