# identity-binder
Service for binding identities across multiple identity providers for the same user.

This project is a web application with two main functions:

1. A user can log in to this webapp using OpenID Connect (OIDC) multiple times simultaneously. They can bind these identities together, letting this Identity Binding Service know that those separate OpenID Connect identities belong to that one user.

2. Other applications that accept OIDC login can query the Identity Binding Service to find out other OIDC identities for that user, if the user has bound them already at the Identity Binding Service. This query is implemented as an OAuth-protected web API.

