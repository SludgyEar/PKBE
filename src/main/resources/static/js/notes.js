
const createNoteForm = document.getElementById('create-note-panel');
// Botón de creación y sus estados
const createNoteButton = document.getElementById('create-note-button');
const contentNormalCreate = document.getElementById('btn-content-normal-create');
const contentLoadingCreate = document.getElementById('btn-content-loading-create');

const tagAddBtn = document.getElementById('tag-add-btn');

let isProcessing = false; // Variable de control para activar o desactivar botones

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
    if(!response.ok){
        throw new Error('Datos inválidos...');
    }
    const data = await response.json();
    document.getElementById('title-input').value = '';
    document.getElementById('content-input').value = '';
    tagsInput.value = '';

    return data;
};

createNoteForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    setCreateNoteLoadingState(true, createNoteButton, contentNormalCreate, contentLoadingCreate);
    try{
        const response = await createNote();
        console.log(response);
    }catch(error){
        alert(error);
    }finally{
        setCreateNoteLoadingState(false, createNoteButton, contentNormalCreate, contentLoadingCreate);
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