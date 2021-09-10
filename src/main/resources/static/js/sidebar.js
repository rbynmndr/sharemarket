/* global bootstrap: false */
(function () {
    'use strict'
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    tooltipTriggerList.forEach(function (tooltipTriggerEl) {
      new bootstrap.Tooltip(tooltipTriggerEl)
    })
  })()

  var navBtn = $("#navigation .nav .nav-link");
  for(var i=0; i<navBtn.length; i++){
    navBtn[i].addEventListener("click", function (){
      var cur = document.getElementsByClassName("active");
      cur[0].className = cur[0].className.replace(" active", "");
      this.className += " active";
    });
  }