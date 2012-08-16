start:	lui	$1, 0			; $1: address of A
		lw	$4, 0($1)		; $4: A, load A to $4
		addi	$1, $1,  4		; address + 4
		lw	$5, 0($1)		; $5: B, load B to $5
		addi	$1, $1,  4		; address + 4
		jal	mul			; call mul subroutine
		sw	$3, 0($1)		; store P
finish:	j	finish		; dead loop
mul:		addi	$2, $0, 16		; $2: counter = 16
		add	$3, $0, $0		; $3: product P
loop:		andi	$6, $5, 1		; 
		beq	$6, $0, shift	; 
		add	$3, $3, $4		; 
shift:	sll	$4, $4, 1		; a = a << 1
		srl	$5, $5, 1		; b = b >> 1
		addi	$2, $2, -1		; counter--
		bne	$2, $0, loop	; go to loop if counter != 0
		jr	$31			; return from subroutine