import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import InputMask from 'react-input-mask';
import { TextField, Button, Grid, Paper } from '@material-ui/core';
import ContatoForm from './ContatoForm';
import { validaCPF, validaDataNascimento } from '../utils/validations';

function PessoaForm({ onSubmit }) {
    const [pessoa, setPessoa] = useState({ nome: '', cpf: '', dataNascimento: '', contatos: [] });
    const [errors, setErrors] = useState({ cpf: '', dataNascimento: '' });
    const navigate = useNavigate();

    const handleChange = (event) => {
        setPessoa({ ...pessoa, [event.target.name]: event.target.value });
        setErrors({ ...errors, [event.target.name]: '' }); 
    };

    const handleContatosChange = (newContatos) => {
        setPessoa(prev => ({ ...prev, contatos: newContatos }));
    };

    const handleSubmit = (event) => {
        event.preventDefault();
    
        let valid = true;
    
        if (!validaCPF(pessoa.cpf || "")) {
            setErrors(prev => ({ ...prev, cpf: 'CPF inválido' }));
            valid = false;
        }
    
        if (!validaDataNascimento(pessoa.dataNascimento || "")) {
            setErrors(prev => ({ ...prev, dataNascimento: 'Data de nascimento inválida' }));
            valid = false;
        }
    
        if (valid) {
            onSubmit(pessoa);
            setPessoa({ nome: '', cpf: '', dataNascimento: '', contatos: [] });
        }
    };

    return (
        <Paper elevation={3} style={{ padding: '1rem' }}>
            <h2>Formulário de Pessoa</h2>
            <form onSubmit={handleSubmit}>
                <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <TextField 
                            fullWidth
                            name="nome"
                            label="Nome"
                            variant="outlined"
                            value={pessoa.nome}
                            onChange={handleChange}
                        />
                    </Grid>
                    <Grid item xs={12} md={6}>
                        <TextField 
                            fullWidth
                            name="cpf"
                            label="CPF"
                            variant="outlined"
                            value={pessoa.cpf}
                            onChange={handleChange}
                            InputProps={{
                                inputComponent: InputMask,
                                inputProps: {
                                    mask: '999.999.999-99',
                                }
                            }}
                            error={!!errors.cpf}
                            helperText={errors.cpf}
                        />
                    </Grid>
                    <Grid item xs={12} md={6}>
                        <TextField 
                            fullWidth
                            type="date"
                            name="dataNascimento"
                            label="Data de Nascimento"
                            variant="outlined"
                            InputLabelProps={{ shrink: true }}
                            value={pessoa.dataNascimento}
                            onChange={handleChange}
                            error={!!errors.dataNascimento}
                            helperText={errors.dataNascimento}
                        />
                    </Grid>
                    <Grid item xs={12}>
                    <ContatoForm contatos={pessoa.contatos} setContatos={handleContatosChange} />
                    </Grid>
                    <Grid item xs={12}>
                        <Button variant="contained" color="primary" type="submit" onClick={handleSubmit}>
                            Salvar
                        </Button>
                        <Button variant="outlined" style={{ marginLeft: '10px' }} onClick={() => navigate('/')}>
                            Voltar para Início
                        </Button>
                    </Grid>
                </Grid>
            </form>
        </Paper>
    );
}

export default PessoaForm;
