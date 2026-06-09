"use client";

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

// const options = [
//   { value: "op1", label: "Opção 1" },
//   { value: "op2", label: "Opção 2" },
//   { value: "op3", label: "Opção 3" },
// ];

export function SelectOpt(props: { options?: { value: string; label: string }[] }   ) {
  const { options = [] } = props;
  return (
    <Select>
      <SelectTrigger className="w-48">
        <SelectValue placeholder="Selecione..." />
      </SelectTrigger>
      <SelectContent>
        {options.map((opt) => (
          <SelectItem key={opt.value} value={opt.value}>
            {opt.label}
          </SelectItem>
        ))}
      </SelectContent>
    </Select>
  );
}