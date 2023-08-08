import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';

function PessoasList() {
    const [pessoas, setPessoas] = useState([]);

    useEffect(() => {
        axios.get('http://localhost:8080/pessoas')
            .then(response => {
                setPessoas(response.data);
            })
            .catch(error => {
                console.error("Erro ao buscar pessoas:", error);
            });
    }, []);

    const handleDelete = (id) => {
        axios.delete(`http://localhost:8080/pessoas/${id}`)
            .then(() => {
                setPessoas(prevPessoas => prevPessoas.filter(pessoa => pessoa.id !== id));
            })
            .catch(error => {
                console.error("Erro ao excluir pessoa:", error);
            });
    }

    return (
        <div>
            <Link to="/add">
                <button variant="contained" style={{ marginBottom: '15px' }}>Adicionar Pessoa</button>
            </Link>
            <ul>
                {pessoas.map(pessoa => (
                    <li key={pessoa.id}>
                        {pessoa.nome}
                        <Link to={`/${pessoa.id}/edit`}>Editar</Link>
                        <button onClick={() => handleDelete(pessoa.id)}>Excluir</button>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default PessoasList;
