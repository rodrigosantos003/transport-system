# Projeto Programação Avançada 2024/25 - Época Normal

[English version](README_EN.md])

## Índice

1. [Autores](#autores)
2. [Resumo do Trabalho](#resumo-do-trabalho)
3. [Estrutura de Dados](#estrutura-de-dados)
4. [Mockup](#mockup)
5. [Documentação](#documentação)
6. [Bibliotecas/Dependências](#bibliotecasdependências)
7. [Java Development Kit](#java-development-kit)

## Autores

- João Fernandes - Nº 202100718
- Rodrigo Santos - Nº 202100722 [Team leader]
- Rúben Dâmaso - Nº 202100723

## Resumo do Trabalho

### Plano de Trabalho

O plano de trabalho, com a respetiva atribuição das tarefas, pode ser consultado [aqui](planoTrabalho.pdf).

### Padrões de Software
O presente projeto foi desenvolvido seguindo o padrão MVC.

### Interpretação da Solução
No presente projeto, existem 4 classes que modelam a solução: `TransportsMap`, `Stop`, `Route` e `Transport`.
O dataset é importado (com recurso a BufferedReader) através da classe `TransportsMap` e, por conseguinte, transposto para um grafo.
Com os dados devolvidos, é apresentada a rede de transportes (i.e grafo) no ecrã.

## Estrutura de Dados

Para suportar a rede de transportes, foi definido um conjunto de classes que permitem representar o problema.

### TransportsMap

A classe `TransportsMap` possui uma relação de herança com GraphEdgeList, de forma a ser possível construir o grafo representante da rede de transportes.
Possui métodos auxiliares para obtenção de informação do grafo.

### Stop

A classe `Stop` representa os vértices do grafo, i.e paragens.

### Route

A classe `Route` representa as arestas do grafo, i.e rotas.

### Transport

A classe `Transport` é um enumerado que representa os tipos de transporte disponíveis

## Mockup
[Mockups Figma](https://www.figma.com/design/6vf2OhBaH9JJQQsLScNpZy/SIT?node-id=0-1&m=dev&t=Gg1asFDGfDcIlbe7-1)

Representação da página inicial:

![Página inicial](./images/mockup_homepage.png)


## Documentação
- [Javadoc](https://sit-javadoc.netlify.app/allclasses-index)
- [Relatório Técnico](./documentation/relatório_técnico.md)

## Bibliotecas/dependências

O projeto tem as seguintes bibliotecas importadas a partir do _Maven_:

- JUnit 5.8.1
- SmartGraph 2.0.0
- OpenCSV 5.9

## Java Development Kit

BellSoft Liberica JDK, versão **17 LTS (FULL)**