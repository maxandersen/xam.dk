---
title: 'Swing requestFocus blues part II'
author: 'Max Rydahl Andersen'

tags: [ Java ]
orignallink: 'http://blog.xam.dk/?p=10'
---
<div><p>hmm - i probably didn't explain my problem very well the last time - or at least not to the point. <br>
But I got one answer though.<br><br>
"Just catch component shown event and then request focus " - Krage Termini<br><br>
Well, this is ok and solves the problem of "when this panel is first shown which component should get the focus"-problem.<br><br>
The other problem of just being able to say somepanel.requestFocus() instead of somepanel.getAField().requestFocus() (which breaks encapsulation) is not solved by this one.<br><br>
Maybe one should just override .requestFocus() on the "somepanel" which delegates the call to the "first field" ? hmm - is that the correct way  ? It sounds "messy" to be forced to inherit from JPanel to encapsulate it's focus mechanism....</p></div>
