# Stally
A quite terrible but complete* language i made,
it sucks balls to code in but it works kinda (no error messages), it has functions, and a very interesting choice of i/o.
its a bad language but the goal was to design a workable language. i dont know much about language design (parsing scares me) but i tried so welp
<br>
*not so sure about that anymore
<br>

Code for it looks kinda cool in a strange way:
e.g. 

```
^4 {
    ^3 {^23 +,}
    ^2 {^22 +,}
    ^1 {^21 +,}
    ^0 {^20 +,}
,}

^11 {^15 {^31 +, ^28 +}, ^15 {^28 +,}}
^10 {^14 {^31 {^30 + ^27 +, ^30 +}, ^31 {^30 +, ^27 +}}, ^14 {^31 {^30 +, ^27 +}, ^31 {^27 +,}}}
^9  {^13 {^30 {^29 + ^26 +, ^29 +}, ^30 {^29 +, ^26 +}}, ^13 {^30 {^29 +, ^26 +}, ^30 {^26 +,}}}
^8  {^12 {^29 {^24 + ^25 +, ^24 +}, ^29 {^24 +, ^25 +}}, ^12 {^29 {^24 +, ^25 +}, ^29 {^25 +,}}}
```

```
<   moves head left
>   moves head right
;   halt (;0 is exit code 0)
{,} if statement {true section, false section}
[]  while
+   writes 1
-   writes 0
!   prints bit
*   inputs bit
@   changes tape
"a" prints utf8 text
£   prints utf8 of cells
$   prints decimal of cells
&   inputs binary number via decimal input
^   moves head index
```
