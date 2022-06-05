package com.myApp.microservice.currrencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {

	@Autowired
	private currencyExchangeproxy proxy;
	
	@GetMapping("currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion Calculate(@PathVariable String from,@PathVariable String to,@PathVariable BigDecimal quantity)
	{
		HashMap<String,String> uriVariables=new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		ResponseEntity<CurrencyConversion> forEntity = new RestTemplate().
		getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversion.class,uriVariables);
		
		CurrencyConversion currencyConversion = forEntity.getBody();
		return new CurrencyConversion(currencyConversion.getId(),
				                      from,to,quantity,
				                      currencyConversion.getConversionMultiple(),
				                      quantity.multiply(currencyConversion.getConversionMultiple()),
				                      currencyConversion.getEnvironment()+"RestTemplate");
		
	}
	
	@GetMapping("currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion CalculateFeign(@PathVariable String from,@PathVariable String to,@PathVariable BigDecimal quantity)
	{

		CurrencyConversion currencyConversion=proxy.retrieveExchangeValue(from,to);
		
		return new CurrencyConversion(currencyConversion.getId(),
				                      from,to,quantity,
				                      currencyConversion.getConversionMultiple(),
				                      quantity.multiply(currencyConversion.getConversionMultiple()),
				                      currencyConversion.getEnvironment()+"Feign");
		
	} 
}
