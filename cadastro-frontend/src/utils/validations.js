/**
 * Validar CPF.
 * 
 * @param {string} cpf O CPF a ser validado.
 * @returns {boolean} Verdadeiro se o CPF for válido, falso caso contrário.
 */
function validaCPF(cpf) {
  if (!cpf || typeof cpf !== "string") return false;

  cpf = cpf.replace(/[^\d]/g, "");

  if (cpf === "" || cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) return false;

  let soma = 0;
  let resto;

  for (let i = 1; i <= 9; i++) 
      soma += parseInt(cpf.substring(i - 1, i)) * (11 - i);
  resto = (soma * 10) % 11;

  if ((resto === 10) || (resto === 11)) resto = 0;
  if (resto !== parseInt(cpf.substring(9, 10))) return false;

  soma = 0;
  for (let i = 1; i <= 10; i++) 
      soma += parseInt(cpf.substring(i - 1, i)) * (12 - i);
  resto = (soma * 10) % 11;

  if ((resto === 10) || (resto === 11)) resto = 0;
  if (resto !== parseInt(cpf.substring(10, 11))) return false;

  return true;
}


/**
* Validar email.
* 
* @param {string} email O email a ser validado.
* @returns {boolean} Verdadeiro se o email for válido, falso caso contrário.
*/
function validaEmail(email) {
  const re = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
  return re.test(String(email).toLowerCase());
}

/**
* Validar número de telefone.
* 
* @param {string} telefone O telefone a ser validado.
* @returns {boolean} Verdadeiro se o telefone for válido, falso caso contrário.
*/
function validaTelefone(telefone) {
  const re = /^\(?([0-9]{2})\)?[-. ]?([0-9]{4,5})[-. ]?([0-9]{4})$/;
  return re.test(telefone);
}

/**
* Validar data de nascimento
* 
* @param {string} data A data de nascimento a ser validada no formato YYYY-MM-DD.
* @returns {boolean} Verdadeiro se a data de nascimento não for futura, falso caso contrário.
*/
function validaDataNascimento(data) {
  const dataAtual = new Date();
  const dataNasc = new Date(data);
  return dataNasc <= dataAtual;
}

export { validaCPF, validaEmail, validaTelefone, validaDataNascimento };