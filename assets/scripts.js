$(function () {
  $('[data-toggle="tooltip"]').tooltip()
})

function addreldate(idx, val) {
   
    var orig = $(val).attr('datetime')
    try {
        var isoDate = new Date(orig).toISOString();
        var rel_date = moment(isoDate).fromNow();
        //  $(val).replaceWith('<span class="pubdate">'+rel_date+'</span>')});
        //  var url = $(this).attr('href');
        $(val).text($(val).text() + ' (' + rel_date + ')');
        $(val).attr("title", $(val).attr('datetime'))
    } catch (e) {
        if (e instanceof RangeError) {
            // ignore
        } else {
            throw e;
        }
    } 
}

$('.dt-published').each(function(idx, val) {
    addreldate(idx, val);
});

$('.dt-updated').each(function(idx, val) {
    addreldate(idx, val);
});

