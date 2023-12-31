import React, { useState, useEffect } from 'react';
import { Button, TextField, Grid, Paper } from '@material-ui/core';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';

function PessoaEdit() {
    const { id } = useParams();
    const [pessoa, setPessoa] = useState({ nome: '', cpf: '', data_nascimento: '', contatos: [{ nome: '', telefone: '', email: '' }] });
    const navigate = useNavigate();

    useEffect(() => {
        axios.get(`http://localhost:8080/api/pessoas/${id}`)
            .then(response => {
                setPessoa(response.data);
            })
            .catch(error => {
                console.error("Erro ao buscar pessoa:", error);
            });
    }, [id]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setPessoa(prevState => ({
            ...prevState,
            [name]: value
        }));
    };

    const handleContatoChange = (index, e) => {
        const { name, value } = e.target;
        const contatos = [...pessoa.contatos];
        contatos[index][name] = value;
        setPessoa(prevState => ({
            ...prevState,
            contatos
        }));
    };

    const handleSubmit = () => {
        axios.put(`http://localhost:8080/api/pessoas/${id}`, pessoa)
            .then(response => {
                console.log('Pessoa atualizada com sucesso:', response.data);
                navigate('/');
            })
            .catch(error => {
                console.error('Erro ao atualizar pessoa:', error);
            });
    };

    return (
        <Paper style={{ padding: 16 }}>
            <h2>Editar Pessoa</h2>

            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <TextField
                        fullWidth
                        name="nome"
                        label="Nome"
                        variant="outlined"
                        value={pessoa.nome}
                        onChange={handleInputChange}
                    />
                </Grid>
                <Grid item xs={12}>
                    <TextField
                        fullWidth
                        name="cpf"
                        label="CPF"
                        variant="outlined"
                        value={pessoa.cpf}
                        onChange={handleInputChange}
                    />
                </Grid>
                <Grid item xs={12}>
                    <TextField
                        fullWidth
                        name="data_nascimento"
                        label="Data de Nascimento"
                        type="date"
                        variant="outlined"
                        value={pessoa.data_nascimento}
                        InputLabelProps={{
                            shrink: true,
                        }}
                        onChange={handleInputChange}
                    />
                </Grid>

                <h3>Contatos</h3>
                {pessoa.contatos.map((contato, index) => (
                    <Grid container spacing={3} key={index}>
                        <Grid item xs={4}>
                            <TextField
                                fullWidth
                                name="nome"
                                label="Nome do Contato"
                                variant="outlined"
                                value={contato.nome}
                                onChange={e => handleContatoChange(index, e)}
                            />
                        </Grid>
                        <Grid item xs={4}>
                            <TextField
                                fullWidth
                                name="telefone"
                                label="Telefone"
                                variant="outlined"
                                value={contato.telefone}
                                onChange={e => handleContatoChange(index, e)}
                            />
                        </Grid>
                        <Grid item xs={4}>
                            <TextField
                                fullWidth
                                name="email"
                                label="Email"
                                variant="outlined"
                                value={contato.email}
                                onChange={e => handleContatoChange(index, e)}
                            />
                        </Grid>
                    </Grid>
                ))}

                <Grid item xs={12}>
                    <Button variant="contained" color="primary" onClick={handleSubmit}>
                        Atualizar
                    </Button>
                </Grid>
            </Grid>
        </Paper>
    );
}

export default PessoaEdit;
