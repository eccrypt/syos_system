package com.syos.domain.model;

public class Product {

	private final String code;
	private final String name;
	private final double price;

	private Product(ProductBuilder builder) {
		this.code = builder.code;
		this.name = builder.name;
		this.price = builder.price;
	   }

	   public Product(String code, String name, double price) {
		this.code = code;
		this.name = name;
		this.price = price;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	public static class ProductBuilder {
		private String code;
		private String name;
		private double price;

		public ProductBuilder code(String code) {
			this.code = code;
			return this;
		}

		public ProductBuilder name(String name) {
			this.name = name;
			return this;
		}

		public ProductBuilder price(double price) {
			this.price = price;
			return this;
		}

		public Product build() {
			return new Product(this);
		}
	}
}
