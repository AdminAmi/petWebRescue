/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

//document.querySelectorAll('.needs-validation').forEach(form => { form.classList.add('was-validated'); });

function aktivirajValidaciju() {
    // Pronalazi sve forme koje imaju klasu 'needs-validation'
    const forme = document.querySelectorAll('.needs-validation');
    
    forme.forEach(form => {
        form.classList.add('was-validated');
    });
}
//
//function prikaziTostove() {
//                var toastElList = [].slice.call(document.querySelectorAll('.toast'));
//                var toastList = toastElList.map(function (toastEl) {
//                    var t = new bootstrap.Toast(toastEl, { autohide: true, delay: 5000 });
//                    t.show();
//                    return t;
//                });
//            }
//
//// Funkcija koja inicijalizuje osluškivače
//function inicijalizujTostove() {
//    // 1. Inicijalno učitavanje
//    prikaziTostove();
//
//    // 2. JSF AJAX podrška
//    if (typeof jsf !== 'undefined') {
//        jsf.ajax.addOnEvent(function(data) {
//            if (data.status === 'success') {
//                prikaziTostove();
//            }
//        });
//    }
//}
//
//// Pokreni čim se DOM učita
//document.addEventListener("DOMContentLoaded", inicijalizujTostove);
