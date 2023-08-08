import React, { useState } from 'react';
import { TextField, Grid } from '@material-ui/core';
import InputMask from 'react-input-mask';
import { validaEmail, validaTelefone } from '../utils/validations';

function ContatoForm({ contatos, setContatos }) {
    const [contato, setContato] = useState({ nome: '', telefone: '', email: '' });
    const [errors, setErrors] = useState({ telefone: '', email: '' });

    const handleChange = (event) => {
        setContato({ ...contato, [event.target.name]: event.target.value });
        setErrors({ ...errors, [event.target.name]: '' });
    };

    const handleAddContato = () => {
        let valid = true;

        if (!validaTelefone(contato.telefone)) {
            setErrors((prev) => ({ ...prev, telefone: 'Telefone inválido' }));
            valid = false;
        }

        if (!validaEmail(contato.email)) {
            setErrors((prev) => ({ ...prev, email: 'Email inválido' }));
            valid = false;
        }

        if (valid) {
            setContatos([...contatos, contato]);
            setContato({ nome: '', telefone: '', email: '' });
        }
    };

    return (
        <div>
            <h3>Contatos</h3>
            <Grid container spacing={3}>
                <Grid item xs={12} md={4}>
                    <TextField 
                        fullWidth
                        name="nome"
                        label="Nome"
                        variant="outlined"
                        value={contato.nome}
                        onChange={handleChange}
                    />
                </Grid>
                <Grid item xs={12} md={4}>
                <TextField 
                        fullWidth
                        name="telefone"
                        label="Telefone"
                        variant="outlined"
                        value={contato.telefone}
                        onChange={handleChange}
                        InputProps={{
                            inputComponent: InputMask,
                            inputProps: {
                                mask: '(99) 99999-9999',
                            }
                        }}
                        error={!!errors.telefone}
                        helperText={errors.telefone}
                    />
                </Grid>
                <Grid item xs={12} md={4}>
                    <TextField 
                        fullWidth
                        name="email"
                        label="Email"
                        type="email"
                        variant="outlined"
                        value={contato.email}
                        onChange={handleChange}
                        error={!!errors.email}
                        helperText={errors.email}
                    />
                </Grid>
            </Grid>
        </div>
    );
}

export default ContatoForm;