# v16.10.5.3 — Correção CatequeseRequest

Correção do isolamento organizacional no `CatequeseWebController`.

`CatequeseRequest` é um record cujo componente de organização é `organizacaoEclesialUuid`, portanto o accessor correto é `organizacaoEclesialUuid()`.

Na criação, a organização informada no request é validada antes da operação. Na atualização, são validadas tanto a organização atual da catequese quanto a organização solicitada no request, impedindo acesso ou movimentação para organização fora do escopo autorizado do usuário.

Sem migration ou alteração de domínio.
