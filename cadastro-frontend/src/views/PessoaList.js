import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';

import { Button, List, ListItem, ListItemText, ListItemSecondaryAction, IconButton } from '@material-ui/core';
import AddIcon from '@material-ui/icons/Add';
import EditIcon from '@material-ui/icons/Edit';
import DeleteIcon from '@material-ui/icons/Delete';

function PessoaList() {
    const [pessoas, setPessoas] = useState([]);

    useEffect(() => {
        axios.get('http://localhost:8080/api/pessoas')
            .then(response => {
                setPessoas(response.data);
            })
            .catch(error => {
                console.error("Erro ao buscar pessoas:", error);
            });
    }, []);

    const handleDelete = (id) => {
        axios.delete(`http://localhost:8080/api/pessoas/${id}`)
            .then(() => {
                setPessoas(prevPessoas => prevPessoas.filter(pessoa => pessoa.id !== id));
            })
            .catch(error => {
                console.error("Erro ao excluir pessoa:", error);
            });
    }

    return (
        <div style={{ padding: '20px' }}>
            <Button
                component={Link}
                to="/add"
                variant="contained"
                color="primary"
                startIcon={<AddIcon />}
                style={{ marginBottom: '15px' }}>
                Adicionar Pessoa
            </Button>
            <List>
                {pessoas.map(pessoa => (
                    <ListItem key={pessoa.id} divider>
                        <ListItemText
                            primary={pessoa.nome}
                        />
                        <ListItemSecondaryAction>
                            <IconButton component={Link} to={`/${pessoa.id}/edit`} edge="end" aria-label="edit">
                                <EditIcon />
                            </IconButton>
                            <IconButton edge="end" aria-label="delete" onClick={() => handleDelete(pessoa.id)}>
                                <DeleteIcon />
                            </IconButton>
                        </ListItemSecondaryAction>
                    </ListItem>
                ))}
            </List>
        </div>
    );
}

export default PessoaList;