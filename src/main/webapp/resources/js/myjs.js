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
