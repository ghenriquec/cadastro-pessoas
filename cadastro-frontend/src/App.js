import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import PessoasList from './views/PessoaList';
import PessoaAdd from './views/PessoaAdd';
import PessoaEdit from './views/PessoaEdit';

function App() {
    return (
        <Router>
            <div style={{ padding: '20px' }}>
                <h1>Cadastro de Pessoas</h1>
                <Routes>
                    <Route path="/" element={<PessoasList />} />
                    <Route path="/add" element={<PessoaAdd />} />
                    <Route path="/:id/edit" element={<PessoaEdit />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
