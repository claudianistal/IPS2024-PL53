package backend.data.acciones;

import java.util.ArrayList;
import java.util.List;

import backend.service.ventas.campanaAccionistas.Accion;

public class DtoAssembler {

	public static List<AccionDTO> toDtoList(List<Accion> acciones, String idAccionista) {
		List<AccionDTO> list = new ArrayList<>();
		for (Accion acc : acciones)
			list.add(toDto(acc, idAccionista));
		
		return list;
	}
	
	public static AccionDTO toDto(Accion acc, String idAccionista) {
		
		AccionDTO dto = new AccionDTO();
		dto.setIdAccion(acc.getIdAccion());
		dto.setPrecio(acc.getPrecio());
		dto.setIdAccionista(idAccionista);
		
		return dto;
	}
}
