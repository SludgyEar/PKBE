// Sections 
const notesViewSection = document.getElementById('notes-view');
const loginViewSection = document.getElementById('login-view');
// Variables de control
let notesLoadedFlag = false; // Variable de control para cargar las notas de un usuario hasta que saen solicitadas
let isProcessing = false; // Variable de control para activar o desactivar botones
// Paneles de Navegación
const createNotePanel = document.getElementById('create-note-panel');
const getNotesPanel = document.getElementById('get-notes-panel');
// Pantallas modales
const notesNotFound = document.getElementById('notes-not-found');
const errorModal = document.getElementById('error-modal');
const expiredSessionModal = document.getElementById('expired-session-modal');
// Botones modales
const reloadModalBtn = document.getElementById('reload-modal-btn');
const expSessionBtn = document.getElementById('exp-session-modal-btn');
// Botones de Navegación
const createNoteButtonNav = document.getElementById('crear-nota-btn');
const getNotesButtonNav = document.getElementById('ver-notas-btn');
// Formularios
const createNoteForm = document.getElementById('create-note-panel');
// Botón de creación y sus estados
const createNoteButton = document.getElementById('create-note-button');
const contentNormalCreate = document.getElementById('btn-content-normal-create');
const contentLoadingCreate = document.getElementById('btn-content-loading-create');

const tagAddBtn = document.getElementById('tag-add-btn');
// Inputs
const tagInputLabel = document.getElementById('tag-input-label');
// labels / párrafos
const createFirstNote = document.getElementById('create-note-cuz-not-found');
createFirstNote.addEventListener('click', () => {
    createNoteButtonNav.click();
});

const setCreateNoteLoadingState = (isLoading, button, normal, loading) => {
    /**
     * Cambiamos el estilo del botón de acceso según su estado
     */
    if (isLoading) {
        button.classList.add('loading');
        button.disabled = true;
        normal.classList.add('hidden');
        loading.classList.remove('hidden');
    } else {
        button.classList.remove('loading');
        button.disabled = false;
        loading.classList.add('hidden');
        normal.classList.remove('hidden');
    }
};

const createNote = async () => {
    //Datos para crear una nota
    const titleInput = document.getElementById('title-input').value.trim();
    const contentInput = document.getElementById('content-input').value.trim();
    const tagsInput = document.getElementById('tags-input');
    // Validamos acceso
    const accessToken = localStorage.getItem('accessToken');
    if(!accessToken){
        throw new Error('Sin acceso...');
    }
    if(tagsInput.value === '' || !tagsInput){
        throw new Error('Ingresa por lo menos una etiqueta...');
    }
    const tags = tagsInput.value.trim();
    const tagsArray = tags.split(',').map(item => item.trim()).filter(item => item !== '');

    const response = await fetch('http://localhost:8080/notes', {
        method: 'POST',
        headers:{
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`
        },
        body: JSON.stringify({ title: titleInput, content: contentInput, tags: tagsArray })
    });
    document.getElementById('title-input').value = '';
    document.getElementById('content-input').value = '';
    tagsInput.value = '';

    if(!response.ok){
        throw new Error('La petición falló inténtalo de nuevo - código: ' + response.status);
    }
    const data = await response.json();

    return data;
};

const getNotes = async () => {
    // Validamos acceso
    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken) {
        throw new Error('Sin acceso...');
    }
    const response = await fetch('http://localhost:8080/notes', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${accessToken}`
        }
    });
    if(!response.ok){
        throw new Error("La petición falló intentalo de nuevo - código: " + response.status);
    }
    const data = await response.json();
    return data;
};

const renderizarDivs = (list) => {
    const contenedor = document.getElementById('note-list-container');
    const htmlItems = list.map(item => `
        <div class="card" id="item-${item.id}">
            <h3>${item.title}</h3>
            <p>${item.content}</p>
            <h4>${item.createdAt}</h4>
            <ul>
                ${item.tags.map(tag => `<li>${tag}</li>`).join('')}
            </ul>
        </div>
        `).join('');
        contenedor.innerHTML = htmlItems;
};

createNoteForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    setCreateNoteLoadingState(true, createNoteButton, contentNormalCreate, contentLoadingCreate);
    try{
        const response = await createNote();
        // TODO: Modal que muestre "Nota creada"
    }catch(error){
        errorModal.classList.remove('hidden');
    }finally{
        setCreateNoteLoadingState(false, createNoteButton, contentNormalCreate, contentLoadingCreate);
        notesLoadedFlag = false;
        /**
         * Cuando se crea una nueva nota desactivamos la bandera de notas cargadas
         * Así, cuando se vuelvan a solicitar se actualiza la lista
         */
    }
});

tagAddBtn.addEventListener('click', () => {
    //Agregar tags
    const tagsInput = document.getElementById('tags-input'); // Contiene todas las tags: "Tag, Tag, Tag,..."
    const tagLabel = document.getElementById('tag-input-label'); // Tag individual

    if (isProcessing) return;
    const tag = tagLabel.value.trim();
    // Validamos que no sea una entrada vacía o que no sea duplicado
    const currentTags = tagsInput.value.split(',').map(t => t.trim());
    if (tag === '' || currentTags.includes(tag)) {
        tagLabel.value = '';
        return;
    }
    // Activamos bloqueo durante la operación
    isProcessing = true;
    tagAddBtn.style.opacity = '0.5';
    tagAddBtn.style.cursor = 'not-allowed';
    tagAddBtn.disabled = true;
    tagLabel.value = '';
    tagLabel.focus();
    // Agregamos etiquetas
    tagsInput.value += (tagsInput.value === '' ? '' : ', ') + tag;
    // Esperar 300ms antes de volver a activar el clic
    setTimeout(() => {
        isProcessing = false;
        tagAddBtn.style.opacity = '1';
        tagAddBtn.style.cursor = 'pointer';
        tagAddBtn.disabled = false;
    }, 300);
});
// Detectar evento de enter en el input
tagInputLabel.addEventListener('keydown', (e) => {
    if(e.key === 'Enter'){
        e.preventDefault();
        tagAddBtn.click();
    }
});

createNoteButtonNav.addEventListener('click', () => {
    getNotesButtonNav.classList.remove('active');
    createNoteButtonNav.classList.add('active');

    getNotesButtonNav.disabled = false;
    createNoteButtonNav.disabled = true;

    createNotePanel.classList.remove('hidden');
    getNotesPanel.classList.add('hidden');
});

getNotesButtonNav.addEventListener('click', async () => {
    createNoteButtonNav.classList.remove('active'); // Conforme haya más botones se deben de agregar los estados
    getNotesButtonNav.classList.add('active');

    createNoteButtonNav.disabled = false;
    getNotesButtonNav.disabled = true;

    createNotePanel.classList.add('hidden');
    getNotesPanel.classList.remove('hidden');

    if (!notesLoadedFlag) {
        try {
            const notes = await getNotes();
            if (notes.length === 0 || !notes) {
                notesNotFound.classList.remove('hidden');
            } else {
                renderizarDivs(notes);
                notesNotFound.classList.add('hidden');
            }
        } catch (error) {
            // Mostrar modal de error
            errorModal.classList.remove('hidden');
        }finally{
            notesLoadedFlag = true;
        }
    }

});



reloadModalBtn.addEventListener('click', async () => {
    errorModal.classList.add('hidden');
    try{
        const response = await fetch('http://localhost:8080/auth/refresh',{
            method: 'GET',
            credentials: 'include'
        });
        if(!response.ok){
            throw new Error('Error durante la renovación de sesión');
        }
        const data = await response.json();
        localStorage.setItem('accessToken', data.accessToken);
        document.location.reload();
    }catch(error){
        expiredSessionModal.classList.remove('hidden');
    }
});

expSessionBtn.addEventListener('click', () => {
    document.localStorage.removeItem('accessToken');
    expiredSessionModal.classList.add('hidden');
    notesViewSection.classList.add('hidden');
    loginViewSection.classList.remove('hidden');
    document.location.reload();
});