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

// SWEET ALERT FOR DELETING CONTACT
const deleteContact = (cid) => {
  Swal.fire({
    title: "Do you want to delete the contact?",
    icon: "question",
    showDenyButton: true,
    confirmButtonText: `Delete`,
    denyButtonText: "Cancel",
  }).then((result) => {
    if (result.isConfirmed) {
      window.location = "/user/delete/" + cid;
    } else if (result.isDenied) {
      Swal.fire("Cancelled", "Your contact has been kept back!", "error");
    }
  });
};
