package com.sistemaestoque.sistema_vendas;

import com.sistemaestoque.sistema_vendas.model.Cliente;
import com.sistemaestoque.sistema_vendas.repository.ClienteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SistemaVendasApplicationTests {

	@Autowired
	private ClienteRepository clienteRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void testaCadastroDeCliente() {
		// Cenário
		Cliente novoCliente = new Cliente();
		novoCliente.setRazaoSocial("Empresa Teste SA");
		novoCliente.setCnpj("11.222.333/0001-44");
		novoCliente.setEmail("contato@empresateste.com"); // Adicionado email
		novoCliente.setEndereco("Rua Teste, 123"); // Adicionado endereço

		// Ação
		Cliente clienteSalvo = clienteRepository.save(novoCliente);

		// Verificação
		Assertions.assertNotNull(clienteSalvo);
		Assertions.assertNotNull(clienteSalvo.getId());
		Assertions.assertEquals("Empresa Teste SA", clienteSalvo.getRazaoSocial());
	}
}