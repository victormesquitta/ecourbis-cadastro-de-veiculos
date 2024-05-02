package br.com.ecourbis.cadastro_veiculos.services;

import br.com.ecourbis.cadastro_veiculos.dtos.VeiculoDTO;
import br.com.ecourbis.cadastro_veiculos.enums.TipoStatus;
import br.com.ecourbis.cadastro_veiculos.enums.TipoUnidade;
import br.com.ecourbis.cadastro_veiculos.mappers.VeiculoDTOMapper;
import br.com.ecourbis.cadastro_veiculos.models.Veiculo;
import br.com.ecourbis.cadastro_veiculos.repositories.VeiculoRepositorio;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VeiculoService {

    private final VeiculoRepositorio veiculoRepositorio;
    private final VeiculoDTOMapper veiculoDTOMapper;

    @Autowired
    public VeiculoService(VeiculoRepositorio veiculoRepositorio, VeiculoDTOMapper veiculoDTOMapper) {
        this.veiculoRepositorio = veiculoRepositorio;
        this.veiculoDTOMapper = veiculoDTOMapper;
    }

    public List<VeiculoDTO> listarVeiculosAtivos(){
        return (veiculoRepositorio.findByStatus(TipoStatus.ATIVO)).stream()
                .map(veiculoDTOMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<VeiculoDTO> listarTodosVeiculos() {
        return (veiculoRepositorio.findAll()).stream()
                .map(veiculoDTOMapper::toDTO)
                .collect(Collectors.toList());
    }

    public VeiculoDTO obterVeiculoPeloID(Integer id) {
        Optional<Veiculo> veiculoOptional = veiculoRepositorio.findById(id);
        veiculoExiste(veiculoOptional);
        return veiculoOptional.map(veiculoDTOMapper::toDTO).orElse(null);
    }

    public void cadastrarVeiculo(VeiculoDTO veiculoDTO) {
        Veiculo veiculo = veiculoDTOMapper.toEntity(veiculoDTO);
        veiculo.setStatus(TipoStatus.ATIVO);
        veiculo.setDtCadastro(LocalDate.now());
        veiculoRepositorio.save(veiculo);
    }

    public void atualizarVeiculo(VeiculoDTO veiculoDTO, Integer id) {
        veiculoExiste(id);
        Veiculo veiculo = veiculoDTOMapper.toEntity(veiculoDTO, id);
        veiculoRepositorio.save(veiculo);
    }

    public void deletarVeiculo(Integer id) {
        VeiculoDTO veiculoDTO = obterVeiculoPeloID(id);
        Veiculo veiculo = veiculoDTOMapper.toEntity(veiculoDTO);
        veiculo.setStatus(TipoStatus.DESATIVADO);
        veiculoRepositorio.save(veiculo);
    }

    public Integer contaVeiculosAtivos(){
        return veiculoRepositorio.countByStatus(TipoStatus.ATIVO);
    }

    public Integer contaVeiculosSul(){
        return veiculoRepositorio.countByUnidade(TipoUnidade.SUL);
    }

    public Integer contaVeiculosLeste(){
        return veiculoRepositorio.countByUnidade(TipoUnidade.LESTE);
    }

    // para métodos update/delete -> o cliente já foi criado e possui um id
    public void veiculoExiste(Integer id){
        Optional<Veiculo> optionalVeiculo = veiculoRepositorio.findById(id);
        if(optionalVeiculo.isEmpty()){
            throw new EntityNotFoundException("Nenhum veículo encontrado para o ID fornecido.");
        }
    }
    // para métodos get -> o cliente já foi criado e possui um id
    public void veiculoExiste(Optional<Veiculo> optionalVeiculo){
        if(optionalVeiculo.isEmpty()){
            throw new EntityNotFoundException("Nenhum veículo encontrado para o ID fornecido.");
        }
    }
    
}