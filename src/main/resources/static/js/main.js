$(document).ready(() => {
  $(".burger").click(() => {
    if ($(".burger").children("i").hasClass("fa-bars")) {
      $(".burger").children("i").removeClass("fa-bars");
      $(".burger").children("i").addClass("fa-times");
    } else {
      $(".burger").children("i").removeClass("fa-times");
      $(".burger").children("i").addClass("fa-bars");
    }
    $(".sidebar").toggleClass("sidebar-active");
    $(".content").toggleClass("content-collapse");
  });
});
