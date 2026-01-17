// Cerrar sesión
const logOutBtn = document.getElementById('cerrar-sesion');
// Cambiar entre la pantalla de login y de registro
const toRegisterBtn = document.getElementById('to-register-btn');
const toLoginBtn = document.getElementById('to-login-btn');
// Sections de pantallas
const loginView = document.getElementById('login-view');
const registerView = document.getElementById('register-view');
const notesView = document.getElementById('notes-view');
toRegisterBtn.addEventListener('click', () => { // Al hacer clic muestra el formulario de registro
    loginView.classList.add('hidden');
    registerView.classList.remove('hidden');
});
toLoginBtn.addEventListener('click', () => { // Al hacer clic muestra el formulario de login
    loginView.classList.remove('hidden');
    registerView.classList.add('hidden');
});

const loginForm = document.getElementById('login-form');
const registerForm = document.getElementById('register-form');

// Botones de envio
const contentNormal = document.getElementById('btn-content-normal');
const contentLoading = document.getElementById('btn-content-loading');
const loginBtn = document.getElementById('login-button');
const registerBtn = document.getElementById('register-button');
const contentNormalReg = document.getElementById('btn-content-normal-reg');
const contentLoadingReg = document.getElementById('btn-content-loading-reg');

const setLoadingState = (isLoading, button, normal, loading) => {

    /**
     * Cambiamos el estilo del botón de acceso según su estado
     */
    if (isLoading) {
        button.classList.add('loading');
        button.disabled = true;
        normal.style.display = 'none';
        loading.style.display = 'inline-flex';
    } else {
        button.classList.remove('loading');
        button.disabled = false;
        loading.style.display = 'none';
        normal.style.display = 'inline-flex';
    }
};

const authenticate = async () => {
    const errorContainer = document.getElementById('error-container');
    const errorText = document.getElementById('error-text');
    const email = document.getElementById('email-input').value;
    const password = document.getElementById('password-input').value;
    /**
     * Validamos las credenciales de un usuario. En caso de ser correctas guardamos
     * su token de acceso para futuros usos. Si es erróneo tiramos una excepción.
     */

    errorContainer.style.display = 'none';
    errorText.value = '';

    const response = await fetch('http://localhost:8080/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email: email, password: password })
    });

    if (!response.ok) {
        throw new Error("Credenciales incorrectas");
    }
    const data = await response.json();
    localStorage.setItem('accessToken', data.accessToken); // Guardamos el token de acceso en el local storage
    loginView.classList.add('hidden');
    registerView.classList.add('hidden');
    notesView.classList.remove('hidden');
};

const register = async () => {
    const errorContainerReg = document.getElementById('error-container-register');
    const errorTextReg = document.getElementById('error-text-register');
    const username = document.getElementById('username-register').value;
    const email = document.getElementById('email-register').value;
    const password = document.getElementById('password-register').value;
    const passwordConfirm = document.getElementById('password-confirm').value;

    if (!username || !email || !password || !passwordConfirm) {
        throw new Error("Datos insuficientes");
    }
    if (password !== passwordConfirm) {
        throw new Error("Las contraseñas deben de coincidir");
    }
    errorContainerReg.style.display = 'none';
    errorTextReg.value = '';
    const response = await fetch('http://localhost:8080/auth/register', {
        method: 'POST',
        headers: {
            'Content-Type': "application/json"
        },
        body: JSON.stringify({ username: username, email: email, passwordHash: password })
    }
    )
    if (!response.ok) {
        throw new Error('Error al registrar usuario');
    }
    loginView.classList.remove('hidden');
    registerView.classList.add('hidden');
};

loginForm.addEventListener('submit', async (e) => {
    const errorContainer = document.getElementById('error-container');
    const errorText = document.getElementById('error-text');
    /**
     * Evento: si se envía el formulario se coloca el estado de carga
     * y se validan las credenciales.
     */
    e.preventDefault();
    setLoadingState(true, loginBtn, contentNormal, contentLoading);
    try {
        await authenticate();

    } catch (error) {
        alert(error);
        errorContainer.style.display = 'inline-flex';
        errorText.innerText = 'Algo ha ocurrido, recargue e intente de nuevo.';
    } finally {
        setLoadingState(false, loginBtn, contentNormal, contentLoading);
    }
});

registerForm.addEventListener('submit', async (e) => {
    const errorContainerReg = document.getElementById('error-container-register');
    const errorTextReg = document.getElementById('error-text-register');
    e.preventDefault();
    setLoadingState(true, registerBtn, contentNormalReg, contentLoadingReg);
    try {
        await register();
        alert('Ingresa tus credenciales');
    } catch (error) {
        alert(error);
        errorContainerReg.style.display = 'inline-flex';
        errorTextReg.innerText = 'Algo ha ocurrido, recargue e intente de nuevo.';
    } finally {
        setLoadingState(false, registerBtn, contentNormalReg, contentLoadingReg);
    }
});

document.addEventListener('DOMContentLoaded', () =>{
    const token = localStorage.getItem('accessToken');
    if(token){
        loginView.classList.add('hidden');
        registerView.classList.add('hidden');
        notesView.classList.remove('hidden');
    }else{
        loginView.classList.remove('hidden');
        registerView.classList.add('hidden');
        notesView.classList.add('hidden');
    }
});

logOutBtn.addEventListener('click', () => {
    const modal = document.getElementById('confirm-modal');
    const cancelBtn = document.getElementById('cancel-modal-btn');
    const confirmBtn = document.getElementById('confirm-modal-btn');

    modal.classList.remove('hidden');

    cancelBtn.addEventListener('click', () => {
        modal.classList.add('hidden');
    });
    confirmBtn.addEventListener('click', () => {
        localStorage.removeItem('accessToken');
        document.location.reload();
    });
});