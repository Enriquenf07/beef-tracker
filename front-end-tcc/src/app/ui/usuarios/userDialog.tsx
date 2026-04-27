"use client"

import { useEffect, useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Plus, Check, Shield, ShieldOff, Mail } from "lucide-react"
import { cn } from "@/lib/utils"



export function UserDialog({ rolesOptions, form, setForm, isPending, onHandleCadastro, onHandleInativar, onHandleReenviarEmail, open, setOpen, selectedRoles, setSelectedRoles }: any) {
  console.log("opt", rolesOptions)
  const toggleRole = (role: string) => {
    setSelectedRoles(prev =>
      prev.includes(role) ? prev.filter(r => r !== role) : [...prev, role]
    )
  }
  console.log('slr', selectedRoles)

  const handleOpenChange = () => {
    setForm({})
    setSelectedRoles([])
    setOpen(prev => !prev)
  }

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogTrigger asChild>
        <Button>
          <Plus className="mr-2 h-4 w-4" />
          Cadastrar
        </Button>
      </DialogTrigger>

      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>
            {!form?.metadata?.id ? "Cadastrar Usuário" : "Editar Usuário"}
          </DialogTitle>
        </DialogHeader>

        {isPending ? (
          <p className="text-sm text-muted-foreground py-4">Carregando...</p>
        ) : (
          <>
            <form
              onSubmit={(e) => {
                onHandleCadastro(e)
              }}
              className="flex flex-col gap-3"
            >
              <input hidden name="id" defaultValue={form?.metadata?.id} />

              {form?.metadata?.id ? (
                <>
                  <Input
                    placeholder="Nome"
                    name="nome"
                    type="text"
                    defaultValue={form?.data?.nome}
                    disabled
                  />
                  <Input
                    placeholder="E-mail"
                    name="email"
                    type="email"
                    defaultValue={form?.data?.email}
                    disabled
                  />
                </>
              ) : (
                <>
                  <Input
                    placeholder="Nome"
                    name="nome"
                    type="text"
                    defaultValue={form?.data?.nome}
                  />
                  <Input
                    placeholder="E-mail"
                    name="email"
                    type="email"
                    defaultValue={form?.data?.email}
                  />
                </>
              )}

              {
                form?.metadata?.id && (
                  <div className="flex flex-col gap-2">
                    <label className="text-sm font-medium text-foreground">
                      Permissões
                    </label>

                    {selectedRoles.length > 0 && (
                      <div className="flex flex-wrap gap-1 mb-1">
                        {selectedRoles.map(role => {
                          const found = rolesOptions.find(r => r.id === role)
                          return (
                            <Badge key={role} variant="secondary" className="text-xs">
                              {found?.nome ?? role}
                            </Badge>
                          )
                        })}
                      </div>
                    )}

                    <div className="grid grid-cols-2 gap-2">
                      {rolesOptions.map(role => {
                        const isSelected = selectedRoles.includes(role.id)
                        return (
                          <button
                            key={role.id}
                            type="button"
                            onClick={() => toggleRole(role.id)}
                            className={cn(
                              "flex items-start gap-2 rounded-md border p-2.5 text-left text-sm transition-all",
                              "hover:bg-accent hover:border-accent",
                              isSelected
                                ? "border-primary bg-primary/5 text-primary"
                                : "border-border bg-background text-muted-foreground"
                            )}
                          >
                            <span className={cn(
                              "mt-0.5 flex h-4 w-4 shrink-0 items-center justify-center rounded border",
                              isSelected
                                ? "border-primary bg-primary text-primary-foreground"
                                : "border-muted-foreground"
                            )}>
                              {isSelected && <Check className="h-3 w-3" />}
                            </span>
                            <span className="flex flex-col">
                              <span className={cn("font-medium", isSelected && "text-primary")}>
                                {role.nome}
                              </span>
                              <span className="text-xs text-muted-foreground leading-tight">
                                {role.descricao}
                              </span>
                            </span>
                          </button>
                        )
                      })}
                    </div>
                  </div>
                )
              }

              <Button type="submit" className="mt-1">Salvar</Button>
            </form>

            {form?.metadata?.id && (
              <Button
                type="button"
                className={cn(
                  form?.data?.ativo
                    ? "bg-destructive hover:bg-destructive/90 text-white"
                    : "bg-emerald-600 hover:bg-emerald-700 text-white"
                )}
                onClick={onHandleInativar}
              >
                {form?.data?.ativo ? (
                  <><ShieldOff className="mr-2 h-4 w-4" /> Inativar</>
                ) : (
                  <><Shield className="mr-2 h-4 w-4" /> Ativar</>
                )}
              </Button>
            )}
            {form?.metadata?.id && !form?.data?.cadastrado && (
              <Button
                type="button"
                className={cn(
                  "bg-transparent hover:bg-secondary/90 text-black"
                )}
                onClick={onHandleReenviarEmail}
              >
                <><Mail className="mr-2 h-4 w-4" /> Reenviar Email</>
              </Button>
            )}
          </>
        )}
      </DialogContent>
    </Dialog>
  )
}