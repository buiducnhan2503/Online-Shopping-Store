package buiducnhan.hutech.Online_Shopping_Store.service;

import buiducnhan.hutech.Online_Shopping_Store.entities.Supplier;
import buiducnhan.hutech.Online_Shopping_Store.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public List<Supplier> getAllSuppliers(){
        return supplierRepository.findAll();
    }

    public Optional<Supplier> getSupplierById(Long id){return supplierRepository.findById(id);}
}
