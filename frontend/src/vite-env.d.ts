/// <reference types="vite/client" />

// Permite importar arquivos .css como módulos TypeScript
declare module "*.module.css" {
  const classes: Record<string, string>;
  export default classes;
}

declare module "*.css" {
  const classes: Record<string, string>;
  export default classes;
}
