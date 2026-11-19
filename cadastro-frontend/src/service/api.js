import axios from 'axios';

// Set REACT_APP_API_URL (e.g. on Vercel) to point the app at the deployed backend.
export const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const fetchAllPessoas = () => {
    return axios.get(`${API_URL}/pessoas`);
}

const fetchPessoaById = (id) => {
    return axios.get(`${API_URL}/pessoas/${id}`);
}

const addPessoa = (pessoa) => {
    return axios.post(`${API_URL}/pessoas`, pessoa);
}

const editPessoa = (id, pessoa) => {
    return axios.put(`${API_URL}/pessoas/${id}`, pessoa);
}

const deletePessoa = (id) => {
    return axios.delete(`${API_URL}/pessoas/${id}`);
}

export {
    fetchAllPessoas,
    fetchPessoaById,
    addPessoa,
    editPessoa,
    deletePessoa
};