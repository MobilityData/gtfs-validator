$(function(){
  $(".accordion tr.notice").on("click", function(){
    $(this).toggleClass("open").next(".description").toggleClass("open");
  });
});