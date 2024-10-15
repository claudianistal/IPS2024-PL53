package shared.gestionHistorial;

import java.awt.CardLayout;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;

import backend.data.CreadorDataService;
import backend.data.productos.ProductoCRUDService;
import backend.data.productos.ProductoDTO;
import backend.data.ventas.VentaDto;
import backend.data.ventas.VentasCRUDService;
import frontend.SwingUtil;
import frontend.historialVentas.HistorialVentas;


public class GestionHistorialShared {
	private HistorialVentas view;
	private List<VentaDto> ventas;
	
	public GestionHistorialShared(HistorialVentas v) {
		this.view = v;
		this.ventas = new ArrayList<VentaDto>();

		this.initView();
	}
	
	/**
	 * Inicializacion del controlador: anyade los manejadores de eventos a los objetos del UI.
	 * Cada manejador de eventos se instancia de la misma forma, para que invoque un metodo privado
	 * de este controlador, encerrado en un manejador de excepciones generico para mostrar ventanas
	 * emergentes cuando ocurra algun problema o excepcion controlada.
	 */
	public void initController() {
		view.getSpInicio().addChangeListener(e -> SwingUtil.exceptionWrapper(() -> controlarFechaInicio()));
		
		view.getSpFin().addChangeListener(e -> SwingUtil.exceptionWrapper(() -> controlarFechaFin()));
		
		view.getBtConfirmarFecha().addActionListener(e -> SwingUtil.exceptionWrapper(() -> showTablaHistorial()));
		
		view.getBtSalir().addActionListener(e -> SwingUtil.exceptionWrapper(() -> confirmar()));
		
		view.getBtSeleccionar().addActionListener(e -> SwingUtil.exceptionWrapper(() -> seleccionarPanel()));
		
		view.getBtAnterior().addActionListener(e -> SwingUtil.exceptionWrapper(() -> accionAnterior()));
		
		view.getTablaResumenVentas().getSelectionModel().addListSelectionListener(e -> SwingUtil.exceptionWrapper(() -> accionSeleccionarFilas()));
		
		view.getBtSalir2().addActionListener(e -> SwingUtil.exceptionWrapper(() -> confirmar()));
		
		view.getBtSalir3().addActionListener(e -> SwingUtil.exceptionWrapper(() -> confirmar()));
		
		view.getBtAnterior2().addActionListener(e -> SwingUtil.exceptionWrapper(() -> accionAnterior()));
	}

	public void initView() {
		
	}
	
	private void controlarFechaInicio() {
	    // Obtener la fecha seleccionada en el spinner de inicio
	    Date inicioDate = (Date) view.getSpInicio().getValue();
	    
	    // Establecer la fecha mínima permitida en el spinner de fin según la fecha de inicio
	    ((SpinnerDateModel) view.getSpFin().getModel()).setStart(inicioDate);
	    
	    // Validar la fecha ingresada manualmente en el spinner de inicio
	    JSpinner.DateEditor editorInicio = (JSpinner.DateEditor) view.getSpInicio().getEditor();
	    try {
	        editorInicio.getFormat().parseObject(editorInicio.getTextField().getText());
	    } catch (ParseException e) {
	        // Manejar el caso de una fecha inválida: restablecer al valor anterior o actual
	        view.getSpInicio().setValue(new Date());
	    }
	}

	private void controlarFechaFin() {
	    // Obtener la fecha seleccionada en el spinner de fin
	    Date finDate = (Date) view.getSpFin().getValue();
	    
	    // Establecer la fecha máxima permitida en el spinner de inicio según la fecha de fin
	    ((SpinnerDateModel) view.getSpInicio().getModel()).setEnd(finDate);
	    
	    // Validar la fecha ingresada manualmente en el spinner de fin
	    JSpinner.DateEditor editorFin = (JSpinner.DateEditor) view.getSpFin().getEditor();
	    try {
	        editorFin.getFormat().parseObject(editorFin.getTextField().getText());
	    } catch (ParseException e) {
	        // Manejar el caso de una fecha inválida: restablecer al valor anterior o actual
	        view.getSpFin().setValue(new Date());
	    }
	}

	
	private void showTablaHistorial() {
		restablecerTablaModelo(view.getTablaResumenVentas());
		ventas.clear();
		VentasCRUDService service = CreadorDataService.getVentasService();
		Date inicio = (Date) view.getSpInicio().getValue();
		Date fin = (Date) view.getSpFin().getValue();
		ventas = service.findVentasFechas(inicio, fin);
		float total = addVentas(ventas);
		view.getTfBalance().setText("Precio Total: " + total + "€");
	}
	
	private float addVentas(List<VentaDto> ventas) {
		float total=0;
		for(VentaDto v : ventas) {
		    Object[] rowData = {v.getConcepto(), v.getFecha(), v.getCoste()};
		    view.getModelVentas().addRow(rowData);
		    total+=v.getCoste();
		}
		return total;
	}
	
	private void addCompraMerchandising(int fila) {
		ProductoCRUDService service = CreadorDataService.getproductoService();
		String id = ventas.get(fila).getIdVenta();
		float total = rellenarTablaMerchandising(service.findProductsByMerchanId(id));
		view.getTfBalance2().setText("Precio Total: " + total + "€");
	}
	
	private float rellenarTablaMerchandising(List<ProductoDTO> productos) {
		float balance=0;
		for(ProductoDTO p : productos) {
			float total = p.getUnidades() * p.getPrecio();
		    Object[] rowData = {p.getNombre(), p.getUnidades(), p.getPrecio(), total};
		    view.getModelMerchandising().addRow(rowData);
		    balance+=total;
		}
		return balance;
	}
	
	private void confirmar() {
		if(confirfmarCancelacion()) {
			System.exit(0);
		}	
	}

	private boolean confirfmarCancelacion() {
		boolean confirmacion=false;
		int response = JOptionPane.showConfirmDialog(null,"¿Estás seguro que quieres salir de la ventana?");
		if(response==JOptionPane.YES_OPTION) {
				confirmacion=true;
		}
		return confirmacion;
	}
	
	private void seleccionarPanel(){
		int filaSeleccionada = view.getTablaResumenVentas().getSelectedRow();
		String concepto = (String) view.getTablaResumenVentas().getValueAt(filaSeleccionada, 0);
		if(concepto.equals("Merchandising")) {
			showPn2(filaSeleccionada);
		}else {
			showPn3();
		}
	}
	
	private void accionSeleccionarFilas() {
		boolean isRowSelected = view.getTablaResumenVentas().getSelectedRow() != -1;
        view.getBtSeleccionar().setEnabled(isRowSelected);
	}
	
	private void accionAnterior() {
		showPn1();
		view.getTablaResumenVentas().clearSelection();
		restablecerTablaModelo(view.getTableResumenCompraMerchan());
	}
	
	private void restablecerTablaModelo(JTable tabla) {
		DefaultTableModel model = (DefaultTableModel) (tabla.getModel());
		model.setRowCount(0);
	}
	
	private void showPn1() {
		((CardLayout)view.getFrmHistorialDeVentas().getContentPane().getLayout()).show(view.getFrmHistorialDeVentas().getContentPane(),"pn1");
	}
	
	private void showPn2(int fila) {
		addCompraMerchandising(fila);
		((CardLayout)view.getFrmHistorialDeVentas().getContentPane().getLayout()).show(view.getFrmHistorialDeVentas().getContentPane(),"pn2");	
	}

	private void showPn3() {
		((CardLayout)view.getFrmHistorialDeVentas().getContentPane().getLayout()).show(view.getFrmHistorialDeVentas().getContentPane(),"pn3");
	}

	
	
}

