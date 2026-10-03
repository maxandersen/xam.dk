// ==UserScript==
// @name           SalesForceDeltaDate
// @namespace      http://www.xam.dk/userscripts
// @description    Looks for things in the page that look like a date and shows a deltadate next to them 
// @include        https://na1.salesforce.com/*
// ==/UserScript==

(function() {
  var replacements, regex, key, textnodes, node, s; 

  replacements = { 
    "(\\d\\d)-(\\d\\d)-(\\d\\d\\d\\d) (\\d\\d):(\\d\\d)": "$1-$2-$3 $4:$5"
}; 

regex = {}; 
for (key in replacements) { 
    regex[key] = new RegExp(key); 
} 

//xpath = "//TD[@class='dataCell']//text()"; // only for table views
xpath = "//TD//text()"; // all dates in TD's
textnodes = document.evaluate(xpath , document, null, XPathResult.UNORDERED_NODE_SNAPSHOT_TYPE, null); 

for (var i = 0; i < textnodes.snapshotLength; i++) { 
    node = textnodes.snapshotItem(i); 
    s = node.data; 
    for (key in replacements) { 
        match = regex[key].exec(s);
//        06-02-2006 18:06
        if(match) {
            day = match[1];
            month = match[2];
            year = match[3];
            hour = match[4];
            minute = match[5];
           // GM_log(day + month + year + hour + minute);
            date = new Date(year, month-1, day, hour, minute, 0);
            now = new Date();
            diff=datediffInText(now, date);
            s = s.replace(regex[key], "$1-$2-$3 $4:$5 (" + diff + ")"); 
        }
        
    } 
    node.data = s; 
} 

})();

function datediffInText(d1,d2) {
    diff = DateDiff("n", now, date, 0, 0);
    minutes = Math.abs(diff);
    if (minutes <= 0) {
      text = "0m";
    } else {
      text = "";
      days = 0;
      //GM_log("d " + minutes + "[" + text + "]");
      if (minutes >= 24*60) {
        days = Math.floor(minutes/(24*60));
        text = text + days + "d";
      }
      if(days<2) {
        minutes = minutes % (24*60);
        //GM_log("h " + minutes + "[" + text + "]");
        if (minutes >= 60) {
            text = text + Math.floor(minutes/(60)) + "h";
        }  
        minutes = minutes % 60;
        //GM_log("m " + minutes + "[" + text + "]");
        text = text + Math.floor(minutes) + "m";
      }
    }
    if(diff>=0) {
      return text;
    } else {
        return "-" + text;
    }
}

/*

Name: jsDate
Desc: VBScript native Date functions emulated for Javascript
Author: Rob Eberhardt, Slingshot Solutions - http://slingfive.com/
History:
2005-08-04v0.94scrapped new dateDiff approach to better match VBScript's simplistic Y/M/Q
2005-08-03v0.93fixed dateDiff/leapyear bug with yyyy/m/q intervals
2004-11-26v0.91fixed datePart/ww bug, added weekdayName() & monthName()
2004-08-30v0.9brand new

*/

// used by dateAdd, dateDiff, datePart, weekdayName, and monthName
// note: less strict than VBScript's isDate, since JS allows invalid dates to overflow (e.g. Jan 32 transparently becomes Feb 1)
function isDate(p_Expression){
return !isNaN(new Date(p_Expression));// <<--- this needs checking
}


// REQUIRES: isDate()
function dateAdd(p_Interval, p_Number, p_Date){
if(!isDate(p_Date)){return "invalid date: '" + p_Date + "'";}
if(isNaN(p_Number)){return "invalid number: '" + p_Number + "'";}

p_Number = new Number(p_Number);
var dt = new Date(p_Date);
switch(p_Interval.toLowerCase()){
case "yyyy": {// year
dt.setFullYear(dt.getFullYear() + p_Number);
break;
}
case "q": {// quarter
dt.setMonth(dt.getMonth() + (p_Number*3));
break;
}
case "m": {// month
dt.setMonth(dt.getMonth() + p_Number);
break;
}
case "y":// day of year
case "d":// day
case "w": {// weekday
dt.setDate(dt.getDate() + p_Number);
break;
}
case "ww": {// week of year
dt.setDate(dt.getDate() + (p_Number*7));
break;
}
case "h": {// hour
dt.setHours(dt.getHours() + p_Number);
break;
}
case "n": {// minute
dt.setMinutes(dt.getMinutes() + p_Number);
break;
}
case "s": {// second
dt.setSeconds(dt.getSeconds() + p_Number);
break;
}
case "ms": {// second
dt.setMilliseconds(dt.getMilliseconds() + p_Number);
break;
}
default: {
return "invalid interval: '" + p_Interval + "'";
}
}
return dt;
}



// REQUIRES: isDate()
// NOT SUPPORTED: firstdayofweek and firstweekofyear (defaults for both)
function dateDiff(p_Interval, p_Date1, p_Date2, p_firstdayofweek, p_firstweekofyear){
if(!isDate(p_Date1)){return "invalid date: '" + p_Date1 + "'";}
if(!isDate(p_Date2)){return "invalid date: '" + p_Date2 + "'";}
var dt1 = new Date(p_Date1);
var dt2 = new Date(p_Date2);

// get ms between dates (UTC) and make into "difference" date
var iDiffMS = dt2.valueOf() - dt1.valueOf();
var dtDiff = new Date(iDiffMS);

// calc various diffs
var nYears  = dt2.getUTCFullYear() - dt1.getUTCFullYear();
var nMonths = dt2.getUTCMonth() - dt1.getUTCMonth() + (nYears!=0 ? nYears*12 : 0);
var nQuarters = parseInt(nMonths/3);//<<-- different than VBScript, which watches rollover not completion

var nMilliseconds = iDiffMS;
var nSeconds = parseInt(iDiffMS/1000);
var nMinutes = parseInt(nSeconds/60);
var nHours = parseInt(nMinutes/60);
var nDays  = parseInt(nHours/24);
var nWeeks = parseInt(nDays/7);


// return requested difference
var iDiff = 0;
switch(p_Interval.toLowerCase()){
case "yyyy": return nYears;
case "q": return nQuarters;
case "m": return nMonths;
case "y": // day of year
case "d": return nDays;
case "w": return nDays;
case "ww":return nWeeks;// week of year// <-- inaccurate, WW should count calendar weeks (# of sundays) between
case "h": return nHours;
case "n": return nMinutes;
case "s": return nSeconds;
case "ms":return nMilliseconds;// millisecond// <-- extension for JS, NOT available in VBScript
default: return "invalid interval: '" + p_Interval + "'";
}
}



// REQUIRES: isDate(), dateDiff()
// NOT SUPPORTED: firstdayofweek and firstweekofyear (does system default for both)
function datePart(p_Interval, p_Date, p_firstdayofweek, p_firstweekofyear){
if(!isDate(p_Date)){return "invalid date: '" + p_Date + "'";}

var dtPart = new Date(p_Date);
switch(p_Interval.toLowerCase()){
case "yyyy": return dtPart.getFullYear();
case "q": return parseInt(dtPart.getMonth()/3)+1;
case "m": return dtPart.getMonth()+1;
case "y": return dateDiff("y", "1/1/" + dtPart.getFullYear(), dtPart);// day of year
case "d": return dtPart.getDate();
case "w": return dtPart.getDay();// weekday
case "ww":return dateDiff("ww", "1/1/" + dtPart.getFullYear(), dtPart);// week of year
case "h": return dtPart.getHours();
case "n": return dtPart.getMinutes();
case "s": return dtPart.getSeconds();
case "ms":return dtPart.getMilliseconds();// millisecond// <-- extension for JS, NOT available in VBScript
default: return "invalid interval: '" + p_Interval + "'";
}
}


// REQUIRES: isDate()
// NOT SUPPORTED: firstdayofweek (does system default)
function weekdayName(p_Date, p_abbreviate){
if(!isDate(p_Date)){return "invalid date: '" + p_Date + "'";}
var dt = new Date(p_Date);
var retVal = dt.toString().split(' ')[0];
var retVal = Array('Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday')[dt.getDay()];
if(p_abbreviate==true){retVal = retVal.substring(0, 3)}// abbr to 1st 3 chars
return retVal;
}
// REQUIRES: isDate()
function monthName(p_Date, p_abbreviate){
if(!isDate(p_Date)){return "invalid date: '" + p_Date + "'";}
var dt = new Date(p_Date);
var retVal = Array('January','February','March','April','May','June','July','August','September','October','November','December')[dt.getMonth()];
if(p_abbreviate==true){retVal = retVal.substring(0, 3)}// abbr to 1st 3 chars
return retVal;
}









// ====================================

// bootstrap different capitalizations
function IsDate(p_Expression){
return isDate(p_Expression);
}
function DateAdd(p_Interval, p_Number, p_Date){
return dateAdd(p_Interval, p_Number, p_Date);
}
function DateDiff(p_interval, p_date1, p_date2, p_firstdayofweek, p_firstweekofyear){
return dateDiff(p_interval, p_date1, p_date2, p_firstdayofweek, p_firstweekofyear);
}
function DatePart(p_Interval, p_Date, p_firstdayofweek, p_firstweekofyear){
return datePart(p_Interval, p_Date, p_firstdayofweek, p_firstweekofyear);
}
function WeekdayName(p_Date){
return weekdayName(p_Date);
}
function MonthName(p_Date){
return monthName(p_Date);
}