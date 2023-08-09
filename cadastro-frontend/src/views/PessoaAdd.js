import React, { useState } from 'react';
import axios from 'axios';
import {
    Button, TextField, Grid, Paper, makeStyles, IconButton
} from '@material-ui/core';
import InputMask from 'react-input-mask';
import AddCircleOutlineIcon from '@material-ui/icons/AddCircleOutline';
import RemoveCircleOutlineIcon from '@material-ui/icons/RemoveCircleOutline';
import { useNavigate } from 'react-router-dom';


const useStyles = makeStyles(theme => ({
    paper: {
        padding: theme.spacing(2),
        textAlign: 'center',
    },
    button: {
        marginTop: theme.spacing(2),
    },
    contactHeader: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        width: '100%',
    },
    contactFields: {
        marginBottom: theme.spacing(2),
    }
}));

function PessoaAdd() {
    const classes = useStyles();
    const navigate = useNavigate();

    const [pessoa, setPessoa] = useState({
        nome: '',
        cpf: '',
        dataNascimento: '',
        contatos: [{ nome: '', telefone: '', email: '' }]
    });

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setPessoa(prevState => ({ ...prevState, [name]: value }));
    };

    const handleContatoChange = (index, e) => {
        const { name, value } = e.target;
        const contatos = [...pessoa.contatos];
        contatos[index][name] = value;
        setPessoa(prevState => ({ ...prevState, contatos }));
    };

    const addContato = () => {
        setPessoa(prevState => ({ ...prevState, contatos: [...prevState.contatos, { nome: '', telefone: '', email: '' }] }));
    };

    const removeContato = (index) => {
        const contatos = [...pessoa.contatos];
        contatos.splice(index, 1);
        setPessoa(prevState => ({ ...prevState, contatos }));
    };

    const handleSubmit = () => {
        axios.post('http://localhost:8080/api/pessoas', pessoa)
            .then(response => {
                console.log('Pessoa adicionada com sucesso:', response.data);
                navigate('/');
            })
            .catch(error => {
                console.error('Erro ao adicionar pessoa:', error);
            });
    };

    return (
        <Paper className={classes.paper}>
            <h2>Adicionar Pessoa</h2>
            <Grid container spacing={3}>
            <Grid item xs={12}>
                    <TextField
                        fullWidth
                        name="nome"
                        label="Nome"
                        variant="outlined"
                        value={pessoa.nome}
                        onChange={handleInputChange}
                        required
                    />
                </Grid>
                <Grid item xs={12}>
                    <InputMask
                        mask="999.999.999-99"
                        value={pessoa.cpf}
                        onChange={handleInputChange}
                    >
                        {(inputProps) => (
                            <TextField
                                {...inputProps}
                                fullWidth
                                name="cpf"
                                label="CPF"
                                variant="outlined"
                                required
                            />
                        )}
                    </InputMask>
                </Grid>
                <Grid item xs={12}>
                    <TextField
                        fullWidth
                        name="dataNascimento"
                        label="Data de Nascimento"
                        type="date"
                        variant="outlined"
                        value={pessoa.dataNascimento}
                        InputLabelProps={{ shrink: true }}
                        onChange={handleInputChange}
                        required
                    />
                </Grid>
                <Grid item xs={12} className={classes.contactFields}>
                    <div className={classes.contactHeader}>
                        <h3>Contatos</h3>
                        <IconButton onClick={addContato}>
                            <AddCircleOutlineIcon />
                        </IconButton>
                    </div>

                    {pessoa.contatos.map((contato, index) => (
                        <Grid container spacing={3} key={index}>
                            <Grid item xs={3}>
                                <TextField
                                    fullWidth
                                    name="nome"
                                    label="Nome do Contato"
                                    variant="outlined"
                                    value={contato.nome}
                                    onChange={e => handleContatoChange(index, e)}
                                    required
                                />
                            </Grid>
                            <Grid item xs={3}>
                                <InputMask
                                    mask="(99) 99999-9999"
                                    value={contato.telefone}
                                    onChange={e => handleContatoChange(index, e)}
                                >
                                    {(inputProps) => (
                                        <TextField
                                            {...inputProps}
                                            fullWidth
                                            name="telefone"
                                            label="Telefone"
                                            variant="outlined"
                                            required
                                        />
                                    )}
                                </InputMask>
                            </Grid>
                            <Grid item xs={3}>
                                <TextField
                                    fullWidth
                                    name="email"
                                    label="Email"
                                    variant="outlined"
                                    type="email"
                                    value={contato.email}
                                    onChange={e => handleContatoChange(index, e)}
                                    required
                                />
                            </Grid>
                            <Grid item xs={3}>
                                <IconButton onClick={() => removeContato(index)}>
                                    <RemoveCircleOutlineIcon />
                                </IconButton>
                            </Grid>
                        </Grid>
                    ))}
                </Grid>
                
                <Grid item xs={12}>
                    <Button variant="contained" color="primary" onClick={handleSubmit}>
                        Adicionar
                    </Button>
                </Grid>
            </Grid>
        </Paper>
    );
}

export default PessoaAdd;
