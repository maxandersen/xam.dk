---
title: 'Todays Favorite Little SQL Gem'
author: 'Max Rydahl Andersen'

tags: [ Java ]
orignallink: 'http://blog.xam.dk/?p=32'
---
<div>
<p>I've always been annoyed by the fact I can't just do something like "drop all tables cascade constraints" or similar in my RDBMS....well, while I've been messing around with some DDL/rev.eng. code where it cames in handy I wanted such a feature for Oracle. <br><br>
I don't remember where I got it from orginally, but here it goes:<br><br></p>
<blockquote>select 'drop ' || object_type || ' "' || object_name || '"'<br>
  || decode(object_type,<br>
       'CLUSTER', ' including tables cascade constraints;',<br>
       'TABLE', ' cascade constraints;',  ';')<br>
from user_objects<br>
where object_type in (<br>
  'CLUSTER', 'TABLE', 'VIEW', 'SEQUENCE', 'SYNONYM', 'FUNCTION',<br>
  'PROCEDURE', 'PACKAGE'<br>
) and object_name not like 'BIN$%';</blockquote>
<br><br>
The script is simple to use, execute it, capture its output and execute that...<br><br>
Cheers<br><br>
P.S. Notice the "not like 'BIN$%', that is because of Oracle 10's painfull recycle bin isn't "dropable" like everything else - and please don't get me started about their lack of JDBC metadata support for identifying them.</div>
